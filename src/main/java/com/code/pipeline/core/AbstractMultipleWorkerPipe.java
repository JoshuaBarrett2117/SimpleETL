/*
授权声明：
本源码系《Java多线程编程实战指南（设计模式篇）第2版》一书（ISBN：978-7-121-38245-1，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtp
http://www.broadview.com.cn/38245
*/

package com.code.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Pipe的多工作者抽象类
 *
 * @param <IN>     输入类型
 * @param <OUT>    工作者输出类型
 * @param <WORKER> 工作者类型
 * @author liufei
 */
public abstract class AbstractMultipleWorkerPipe<IN, OUT, WORKER extends AbstractWorker> extends AbstractPipe<IN, OUT> {
    final static Logger logger = LoggerFactory.getLogger(AbstractMultipleWorkerPipe.class);
    protected final BlockingQueue<IN> workQueue;
    protected final WORKER[] workers;
    protected final TerminationToken terminationToken = new TerminationToken(false);
    protected final Set<AbstractTerminatableThread> workerThreads = new HashSet<>();
    protected final ThreadPoolExecutor pool
            = new ThreadPoolExecutor(5, 10, 100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),
            (r) -> {
                return new Thread(r, getName());
            });

    public AbstractMultipleWorkerPipe(String name, WORKER... workers) {
        this(new SynchronousQueue<>(), name, workers);
    }

    public AbstractMultipleWorkerPipe(BlockingQueue<IN> workQueue, String name, WORKER... workers) {
        super(name);
        this.workQueue = workQueue;
        this.workers = workers;
        for (int i = 0; i < workers.length; i++) {
            WORKER worker = workers[i];
            workerThreads.add(new AbstractTerminatableThread(workers[i].getName(), terminationToken) {
                @Override
                protected void doRun() throws Exception {
                    try {
                        AbstractMultipleWorkerPipe.this.doRun(worker);
                    } finally {
                        terminationToken.reservations.decrementAndGet();
                    }
                }

                @Override
                protected void doTerminiate() {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}]准备关闭...", getName());
                    }
                    worker.shutdown();
                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}]已关闭...", getName());
                    }
                }
            });
        }
    }

    protected abstract void doRun(WORKER worker) throws PipeException, InterruptedException;


    @Override
    public void process(IN input) throws InterruptedException {
        terminationToken.reservations.incrementAndGet();
        try {
            workQueue.put(input);
        } catch (InterruptedException e) {
            terminationToken.reservations.decrementAndGet();
            throw e;
        }
    }

    @Override
    public void init(PipeContext pipeCtx) {
        for (AbstractTerminatableThread thread : workerThreads) {
            pool.execute(thread::run);
        }
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}]任务，等待所有工作者执行完成", getName());
        }
        while (terminationToken.reservations.get() > 0) {
        }
        //必须等所有工作者线程执行完毕
        pool.shutdown();
//        while (!pool.isTerminated()) {
//        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}]任务，所有工作者执行完成", getName());
        }
    }
}
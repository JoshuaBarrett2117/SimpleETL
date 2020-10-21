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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Pipe的多工作者抽象类
 *
 * @param <IN>  输入类型
 * @param <OUT> 输出类型
 * @author liufei
 */
public abstract class AbstractMultipleWorkerPipe<IN, OUT> extends AbstractPipe<IN, OUT> {
    protected final BlockingQueue<IN> workQueue;
    private final TerminationToken terminationToken = new TerminationToken();
    private final Set<AbstractTerminatableThread> workerThreads = new HashSet<AbstractTerminatableThread>();

    public AbstractMultipleWorkerPipe(String name, IWorker<IN, OUT>... workers) {
        this(new SynchronousQueue<>(), name, workers);
    }

    public AbstractMultipleWorkerPipe(BlockingQueue<IN> workQueue, String name, IWorker<IN, OUT>... workers) {
        super(name);
        this.workQueue = workQueue;
        for (int i = 0; i < workers.length; i++) {
            final int finalIndex = i;
            workerThreads.add(new AbstractTerminatableThread(terminationToken) {
                @Override
                protected void doRun() throws Exception {
                    try {
                        IN input = workQueue.take();
                        OUT out = workers[finalIndex].doRun(input);
                        if (null != nextPipe) {
                            if (null != out) {
                                ((Pipe<OUT, ?>) nextPipe).process(out);
                            }
                        }
                    } finally {
                        terminationToken.reservations.decrementAndGet();
                    }
                }
            });
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void process(IN input) throws InterruptedException {
        workQueue.put(input);
        terminationToken.reservations.incrementAndGet();
    }

    @Override
    public void init(PipeContext pipeCtx) {
        for (AbstractTerminatableThread thread : workerThreads) {
            thread.start();
        }
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        while (terminationToken.reservations.get() > 0) {

        }
        for (AbstractTerminatableThread thread : workerThreads) {
            thread.terminate();
            try {
                thread.join(TimeUnit.MILLISECONDS.convert(timeout, unit));
            } catch (InterruptedException e) {
            }
        }
    }
}
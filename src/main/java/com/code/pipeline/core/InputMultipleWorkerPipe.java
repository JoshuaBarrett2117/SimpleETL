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

import com.code.pipeline.worker.AbstractInputWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Pipe的多输入工作者抽象类
 *
 * @param <OUT> 输出类型
 * @author liufei
 */
public class InputMultipleWorkerPipe<OUT> extends AbstractMultipleWorkerPipe<Void, OUT, AbstractInputWorker<OUT>> {
    private static final Logger logger = LoggerFactory.getLogger(InputMultipleWorkerPipe.class);
    private CyclicBarrier cyclicBarrier;

    public InputMultipleWorkerPipe(BlockingQueue<Void> workQueue, String name, AbstractInputWorker<OUT>... workers) {
        super(workQueue, name, workers);
        cyclicBarrier = new CyclicBarrier(workers.length);
    }

    public InputMultipleWorkerPipe(String name, AbstractInputWorker... workers) {
        super(name, workers);
        cyclicBarrier = new CyclicBarrier(workers.length);
    }

    @Override
    public void init(PipeContext pipeCtx) {
        super.init(pipeCtx);
    }

    @Override
    protected void doRun(AbstractInputWorker<OUT> worker) throws PipeException, InterruptedException {
        int count = 0;
        if (null != nextPipe) {
            if (null != worker) {
                while (worker.hasNext()) {
                    ((Pipe<OUT, ?>) nextPipe).process(worker.next());
                    count++;
                }
            }
        }
        try {
            //设置栅栏，当所有工作者任务都完成时，通过栅栏
            logger.info("[{}]数据抽取完成，共[{}]条，等待主线程中断", worker.getName(), count);
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(Void input) throws InterruptedException {
        terminationToken.reservations.addAndGet(workers.length);
    }
}
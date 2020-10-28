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

import com.code.pipeline.etl.AbstractInputWorker;

/**
 * Pipe的多工作者抽象类
 *
 * @param <OUT> 输出类型
 * @author liufei
 */
public abstract class AbstractInputMultipleWorkerPipe<OUT> extends AbstractMultipleWorkerPipe<Void, OUT, AbstractInputWorker<OUT>> {


    public AbstractInputMultipleWorkerPipe(String name, AbstractInputWorker... workers) {
        super(name, workers);
    }

    @Override
    protected void doRun(AbstractInputWorker<OUT> worker) throws PipeException, InterruptedException {
        if (null != nextPipe) {
            if (null != worker) {
                while (worker.hasNext()) {
                    ((Pipe<OUT, ?>) nextPipe).process(worker.next());
                }
            }
        }
    }

    @Override
    public void process(Void input) throws InterruptedException {
        terminationToken.reservations.incrementAndGet();
    }
}
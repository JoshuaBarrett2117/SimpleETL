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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于线程池的Pipe实现类。
 *
 * @param <IN>  输入类型
 * @param <OUT> 输出类型
 * @author Viscent Huang
 */
public class ThreadPoolPipeDecorator<IN, OUT> implements Pipe<IN, OUT> {
    private final Pipe<IN, OUT> delegate;
    private final ExecutorService executorSerivce;
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolPipeDecorator.class);

    //线程池停止标志。
    private final TerminationToken terminationToken;

    public ThreadPoolPipeDecorator(Pipe<IN, OUT> delegate,
                                   ExecutorService executorSerivce) {
        this.delegate = delegate;
        this.executorSerivce = executorSerivce;
        this.terminationToken = TerminationToken.newInstance(executorSerivce);
    }

    @Override
    public void init(PipeContext pipeCtx) {
        delegate.init(pipeCtx);
    }

    @Override
    public void process(final IN input) throws InterruptedException {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    delegate.process(input);
                } catch (InterruptedException e) {
                    ;
                } finally {
                    terminationToken.reservations.decrementAndGet();
                }
            }

        };

        executorSerivce.submit(task);
        terminationToken.reservations.incrementAndGet();

    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        terminationToken.setIsToShutdown();
        logger.info("[{}]:等待关闭线程池", ((AbstractPipe) delegate).name);
        while (terminationToken.reservations.get() > 0) {

        }
        logger.info("[{}]:完成", ((AbstractPipe) delegate).name);
        delegate.shutdown(timeout, unit);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    /**
     * 线程池停止标志。
     * 每个ExecutorService实例对应唯一的一个TerminationToken实例。
     * 这里使用了Two-phase Termination模式（第5章）的思想来停止多个Pipe实例所共用的
     * 线程池实例。
     *
     * @author Viscent Huang
     */
    private static class TerminationToken extends com.code.pipeline.core.TerminationToken {
        private final static ConcurrentMap<ExecutorService, TerminationToken> INSTANCES_MAP  = new ConcurrentHashMap<ExecutorService, TerminationToken>();

        // 私有构造器
        private TerminationToken() {

        }

        void setIsToShutdown() {
            this.toShutdown = true;
        }

        static TerminationToken newInstance(ExecutorService executorSerivce) {
            TerminationToken token = INSTANCES_MAP.get(executorSerivce);
            if (null == token) {
                token = new TerminationToken();
                TerminationToken existingToken = INSTANCES_MAP.putIfAbsent(
                        executorSerivce, token);
                if (null != existingToken) {
                    token = existingToken;
                }
            }
            return token;
        }
    }

}

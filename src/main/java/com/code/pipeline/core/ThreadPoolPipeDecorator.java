package com.code.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 带线程池的管道
 *
 * @param <IN>
 * @param <OUT>
 */
public class ThreadPoolPipeDecorator<IN, OUT> implements Pipe<IN, OUT> {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolPipeDecorator.class);
    private String name = "->";
    private final Pipe<IN, OUT> delegate;
    private final ExecutorService executorService;

    // 线程池停止标志。
    private final TerminationToken terminationToken;
    private final CountDownLatch stageProcessDoneLatch = new CountDownLatch(1);

    public ThreadPoolPipeDecorator(Pipe<IN, OUT> delegate, ExecutorService executorService) {
        this.delegate = delegate;
        this.executorService = executorService;
        this.terminationToken = TerminationToken.newInstance(executorService);
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
                int remainingReservations = 0;
                try {
                    delegate.process(input);
                } catch (InterruptedException e) {
                    logger.error("发生异常，中断线程", e);
                    Thread.currentThread().interrupt();
                } finally {
                    //任务完成后-1
                    remainingReservations = terminationToken.reservations.decrementAndGet();
                    logger.info("剩余任务数量是：" + remainingReservations);
                }
                if (terminationToken.isToShutdown() && 0 == remainingReservations) {
                    stageProcessDoneLatch.countDown();
                }
            }
        };

        executorService.execute(task);
        int i = terminationToken.reservations.incrementAndGet();
        logger.info("当前任务数量是：" + i);

    }

    @Override
    public void over() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                int remainingReservations = -1;
                try {
                    delegate.over();
                } catch (InterruptedException e) {
                    logger.error("发生异常，中断线程", e);
                    Thread.currentThread().interrupt();
                } finally {
                    //任务完成后-1
                    remainingReservations = terminationToken.reservations.decrementAndGet();
                    logger.info("剩余任务数量是：" + remainingReservations);
                }
                if (terminationToken.isToShutdown() && 0 == remainingReservations) {
                    stageProcessDoneLatch.countDown();
                }
            }
        };

        executorService.execute(task);
        terminationToken.reservations.incrementAndGet();
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        terminationToken.setIsToShutdown();

        if (terminationToken.reservations.get() > 0) {
            try {
                if (stageProcessDoneLatch.getCount() > 0) {
                    logger.info("Decorator调用管道的关闭方法的线程是：  " + Thread.currentThread().getName());
                    stageProcessDoneLatch.await(timeout, unit);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Decorator调用delegate的关闭方法的线程是：  " + Thread.currentThread().getName());
        delegate.shutdown(timeout, unit);
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        delegate.setNextPipe(nextPipe);
    }

    /**
     * 线程池停止标志。 每个ExecutorService实例对应唯一的一个TerminationToken实例。 这里使用了Two-phase
     * Termination模式（第5章）的思想来停止多个Pipe实例所共用的 线程池实例。
     *
     * @author Viscent Huang
     */
    private static class TerminationToken extends GeneralTerminationToken {
        private final static ConcurrentMap<ExecutorService, TerminationToken> INSTANCES_MAP = new ConcurrentHashMap<ExecutorService, TerminationToken>();

        // 私有构造器
        private TerminationToken() {

        }

        void setIsToShutdown() {
            this.toShutdown = true;
        }

        static TerminationToken newInstance(ExecutorService executorService) {
            TerminationToken token = INSTANCES_MAP.get(executorService);
            if (null == token) {
                token = new TerminationToken();
                TerminationToken existingToken = INSTANCES_MAP.putIfAbsent(executorService, token);
                if (null != existingToken) {
                    token = existingToken;
                }
            }
            logger.info(Thread.currentThread().getName() + token);
            return token;
        }
    }

}
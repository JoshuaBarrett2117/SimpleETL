package com.code.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 简单的管道线实现，简单的组织管道线
 *
 * @param <T>
 * @param <OUT>
 */
public class SimplePipeline<T, OUT> extends AbstractPipe<T, OUT> implements Pipeline<T, OUT> {
    private static final Logger logger = LoggerFactory.getLogger(SimplePipeline.class);
    /**
     * 管道队列
     */
    protected final Queue<Pipe<?, ?>> pipes = new LinkedList<Pipe<?, ?>>();

    /**
     * 线程池服务
     */
    protected final ExecutorService helperExecutor;

    public SimplePipeline() {
        this(Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "SimplePipeline-Helper");
                t.setDaemon(false);
                return t;
            }
        }));
    }

    public SimplePipeline(final ExecutorService helperExecutor) {
        super();
        this.helperExecutor = helperExecutor;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        Pipe<?, ?> pipe;
        //从队列中一个个移除管道并关闭管道
        while (null != (pipe = pipes.poll())) {
            pipe.shutdown(timeout, unit);
        }
        helperExecutor.shutdown();
    }

    /**
     * 继承自Pipe的方法，每个管道线也是一个管道，当前管道没有要执行的逻辑，放空
     *
     * @param input 输入元素（任务）
     * @return
     * @throws PipeException
     */
    @Override
    protected OUT doProcess(T input) throws PipeException {
        // 什么也不做
        return null;
    }

    @Override
    public void addPipe(Pipe<?, ?> pipe) {
        // Pipe间的关联关系在init方法中建立
        pipes.add(pipe);
    }


    /**
     * 带线程池的管道装饰
     *
     * @param delegate
     * @param executorService
     * @param <INPUT>
     * @param <OUTPUT>
     */
    public <INPUT, OUTPUT> void addAsThreadPoolBasedPipe(Pipe<INPUT, OUTPUT> delegate, ExecutorService executorService) {
        addPipe(new ThreadPoolPipeDecorator<INPUT, OUTPUT>(delegate, executorService));
    }

    /**
     * 执行管道线
     *
     * @param input
     * @throws InterruptedException
     */
    @Override
    public void process(T input) throws InterruptedException {
        //从队列中取出一个头部的管道
        Pipe<T, ?> firstPipe = (Pipe<T, ?>) pipes.peek();
        firstPipe.process(input);
    }

    /**
     * 初始化管道上下文
     *
     * @param ctx
     */
    @Override
    public void init(final PipeContext ctx) {
        LinkedList<Pipe<?, ?>> pipesList = (LinkedList<Pipe<?, ?>>) pipes;
        Pipe<?, ?> prevPipe = this;
        for (Pipe<?, ?> pipe : pipesList) {
            prevPipe.setNextPipe(pipe);
            prevPipe = pipe;
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (Pipe<?, ?> pipe : pipes) {
                    pipe.init(ctx);
                }
            }
        };
        //submit有返回值，而execute没有，submit方便Exception处理
        helperExecutor.submit(task);
    }

    public PipeContext newDefaultPipelineContext() {
        return new PipeContext() {
            @Override
            public void handleError(final PipeException exp) {
                helperExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        exp.printStackTrace();
                    }
                });
            }
        };
    }
}
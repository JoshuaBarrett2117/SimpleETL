package com.code.pipeline;

import java.util.concurrent.TimeUnit;

/**
 * 管道的抽象类
 *
 * @param <IN>
 * @param <OUT>
 */
public abstract class AbstractPipe<IN, OUT> implements Pipe<IN, OUT> {
    protected volatile Pipe<?, ?> nextPipe = null;
    protected volatile PipeContext pipeCtx;

    @Override
    public void init(PipeContext pipeCtx) {
        this.pipeCtx = pipeCtx;
    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        this.nextPipe = nextPipe;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        // 什么也不做
    }

    /**
     * 留给子类实现。用于子类实现其任务处理逻辑。
     *
     * @param input 输入元素（任务）
     * @return 任务的处理结果
     * @throws PipeException
     */
    protected abstract OUT doProcess(IN input) throws PipeException;

    @Override
    public void process(IN input) throws InterruptedException {
        try {
            OUT out = doProcess(input);
            if (null != nextPipe) {
                if (null != out) {
                    ((Pipe<OUT, ?>) nextPipe).process(out);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (PipeException e) {
            pipeCtx.handleError(e);
        }
    }
}
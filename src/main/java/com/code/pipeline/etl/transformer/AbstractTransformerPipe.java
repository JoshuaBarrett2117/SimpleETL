package com.code.pipeline.etl.transformer;

import com.code.pipeline.core.AbstractPipe;
import com.code.pipeline.core.Pipe;
import com.code.pipeline.core.PipeException;

/**
 * 转换管道的抽象类
 *
 * @param <IN>
 * @param <OUT>
 */
public abstract class AbstractTransformerPipe<IN, OUT> extends AbstractPipe<IN, OUT> {

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
package com.code.pipeline.etl.transformer;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractPipe;
import com.code.pipeline.core.Pipe;
import com.code.pipeline.core.PipeException;

/**
 * 转换管道的抽象类
 */
public abstract class AbstractTransformerPipe extends AbstractPipe<DataRowModel, DataRowModel> {

    /**
     * 留给子类实现。用于子类实现其任务处理逻辑。
     *
     * @param input 输入元素（任务）
     * @return 任务的处理结果
     * @throws PipeException
     */
    protected abstract DataRowModel doProcess(DataRowModel input) throws PipeException;

    @Override
    public void process(DataRowModel input) throws InterruptedException {
        try {
            DataRowModel out = doProcess(input);
            if (null != nextPipe) {
                if (null != out) {
                    ((Pipe<DataRowModel, ?>) nextPipe).process(out);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (PipeException e) {
            pipeCtx.handleError(e);
        }
    }
}
package com.code.pipeline.core;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public interface ITransformerWorker<IN, OUT> extends Worker {
    OUT doRun(IN in) throws PipeException;
}

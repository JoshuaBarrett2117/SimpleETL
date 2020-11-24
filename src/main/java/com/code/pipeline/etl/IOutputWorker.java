package com.code.pipeline.etl;

import com.code.pipeline.core.ITransformerWorker;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public interface IOutputWorker<IN> extends ITransformerWorker<IN, Void> {
}

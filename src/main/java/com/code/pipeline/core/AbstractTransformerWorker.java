package com.code.pipeline.core;

/**
 * 基础流程处理工作者
 * @author liufei
 */
public abstract class AbstractTransformerWorker<IN, OUT> extends AbstractWorker implements ITransformerWorker<IN, OUT> {

    public AbstractTransformerWorker(String name) {
        super(name);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}

package com.code.pipeline.core;

import java.util.concurrent.TimeUnit;

/**
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
    public void shutdown(long timeout, TimeUnit unit) {

    }

}

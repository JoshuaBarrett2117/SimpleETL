package com.code.pipeline.core;

import java.util.concurrent.TimeUnit;

/**
 * @author liufei
 */
public abstract class AbstractWorker<IN, OUT> implements IWorker<IN, OUT> {
    private String name;

    public AbstractWorker(String name) {
        this.name = name;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }
}

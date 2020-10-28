package com.code.pipeline.core;

import java.util.concurrent.TimeUnit;

/**
 * @author liufei
 */
public abstract class AbstractWorker implements Worker {
    protected String name;

    public AbstractWorker(String name) {
        this.name = name;
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

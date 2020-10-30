package com.code.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础工作者
 *
 * @author liufei
 */
public abstract class AbstractWorker implements Worker {
    private static final Logger logger = LoggerFactory.getLogger(AbstractWorker.class);
    protected String name;

    public AbstractWorker(String name) {
        this.name = name;
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {
        logger.info("[{}]工作者关闭", name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

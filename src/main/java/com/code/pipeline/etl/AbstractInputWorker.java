package com.code.pipeline.etl;

import com.code.pipeline.core.AbstractWorker;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public abstract class AbstractInputWorker<OUT> extends AbstractWorker implements IInputWorker<OUT> {

    public AbstractInputWorker(String name) {
        super(name);
    }

}

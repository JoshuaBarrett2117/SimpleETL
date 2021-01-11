package com.code.pipeline.worker;

import com.code.pipeline.core.Worker;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public interface IInputWorker<DATA> extends Worker {
    boolean hasNext();

    DATA next();
}

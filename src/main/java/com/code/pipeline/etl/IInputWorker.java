package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
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

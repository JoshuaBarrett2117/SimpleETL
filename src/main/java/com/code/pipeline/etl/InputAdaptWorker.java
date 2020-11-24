package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataSource;

import java.util.Iterator;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class InputAdaptWorker extends AbstractInputWorker<DataRowModel> {
    Iterator<DataRowModel> iterator;

    public InputAdaptWorker(String name, IDataSource dataSource, IDataSource.Exp e) {
        super(name);
        iterator = dataSource.iterator(e);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DataRowModel next() {
        return iterator.next();
    }
}

package com.code.tooltrans.common.target;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;

public abstract class AbstractTarget implements IDataTarget {


    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
        throw new RuntimeException("暂不支持");
    }

    @Override
    public boolean update(List<DataRowModel> docs, String indexName, String idField) {
        throw new RuntimeException("暂不支持");
    }

}

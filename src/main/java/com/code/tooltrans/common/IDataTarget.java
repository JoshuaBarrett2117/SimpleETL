package com.code.tooltrans.common;


import com.code.common.dao.core.model.DataRowModel;

import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:08
 */
public interface IDataTarget {
    boolean save(List<DataRowModel> docs, String indexName);

    boolean saveOrUpdate(List<DataRowModel> docs, String indexName);
    boolean update(List<DataRowModel> docs, String indexName,String idField);

    boolean close();
}

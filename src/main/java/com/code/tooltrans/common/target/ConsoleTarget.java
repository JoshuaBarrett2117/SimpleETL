package com.code.tooltrans.common.target;

import com.alibaba.fastjson.JSONObject;
import com.code.common.dao.core.model.DataRowModel;

import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 11:06
 */
public class ConsoleTarget extends AbstractTarget {
    @Override
    public boolean save(List<DataRowModel> docs, String indexName) {
        for (DataRowModel doc : docs) {
            System.out.println("indexName: " + indexName + "【" + JSONObject.toJSONString(doc) + "】");
        }
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
        return save(docs, indexName);
    }

    @Override
    public boolean close() {
        return true;
    }
}

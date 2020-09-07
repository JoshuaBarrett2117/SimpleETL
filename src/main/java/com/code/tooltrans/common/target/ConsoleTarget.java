package com.code.tooltrans.common.target;

import com.alibaba.fastjson.JSONObject;
import com.code.common.dao.core.model.DomainElement;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 11:06
 */
public class ConsoleTarget implements IDataTarget {
    @Override
    public boolean save(List<DomainElement> docs, String indexName) {
        for (DomainElement doc : docs) {
            System.out.println("indexName: " + indexName + "【" + JSONObject.toJSONString(doc) + "】");
        }
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DomainElement> docs, String indexName) {
        return save(docs, indexName);
    }

    @Override
    public boolean close() {
        return true;
    }
}
package com.code.pipeline.etl.renyuan.worker;

import com.code.common.dao.core.model.DataRowModel;

import java.util.Map;

/**
 * 文件名称: DefaultFieldWarp.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/11/4     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class DefaultFieldWarp implements IFieldWarp {
    @Override
    public void warp(Map<String, Object> target, Map<String, Object> input, FieldMapper fieldMapper) {
        if (input.containsKey(fieldMapper.srcField) && input.get(fieldMapper.srcField) != null) {
            if (fieldMapper.type == null) {
                target.put(fieldMapper.targetField,  input.get(fieldMapper.srcField));
            }
            if (input.get("RIGHT_TYPE").equals(fieldMapper.type)) {
                target.put(fieldMapper.targetField, input.get(fieldMapper.srcField));
            }
        }
    }
}
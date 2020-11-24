package com.code.pipeline.etl.renyuan.worker;

import com.code.common.dao.core.model.DataRowModel;

import java.util.Map;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
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
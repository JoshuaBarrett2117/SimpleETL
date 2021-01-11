package com.code.pipeline.worker.renyuan.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class CphFieldWarp implements IFieldWarp {
    @Override
    public void warp(Map<String, Object> target, Map<String, Object> input, FieldMapper fieldMapper) {
        if (isMatch(input, fieldMapper)) {
            if (target.containsKey(fieldMapper.targetField)) {
                ((List<String>) target.get(fieldMapper.targetField)).add((String) input.get(fieldMapper.srcField));
            } else {
                List<String> cphs = new ArrayList<>();
                cphs.add((String) input.get(fieldMapper.srcField));
                target.put(fieldMapper.targetField, cphs);
            }
        }
    }

    private boolean isMatch(Map<String, Object> input, FieldMapper fieldMapper) {
        return input.containsKey(fieldMapper.srcField)
                && input.get(fieldMapper.srcField) != null
                && input.get("RIGHT_TYPE").equals(fieldMapper.type)
                ;
    }
}

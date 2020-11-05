package com.code.pipeline.etl.renyuan.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件名称: CphFieldWarp.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/11/4     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
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

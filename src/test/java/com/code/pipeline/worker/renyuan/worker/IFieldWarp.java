package com.code.pipeline.worker.renyuan.worker;

import java.util.Map;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public interface IFieldWarp {
    void warp(Map<String, Object> target, Map<String, Object> input, FieldMapper fieldMapper);
}

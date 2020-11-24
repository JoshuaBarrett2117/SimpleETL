package com.code.pipeline.etl.renyuan.worker;

import com.code.common.dao.core.model.DataRowModel;

import java.util.Map;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public interface IFieldWarp {
    void warp(Map<String, Object> target, Map<String, Object> input, FieldMapper fieldMapper);
}

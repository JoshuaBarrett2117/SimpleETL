package com.code.pipeline.etl.renyuan.worker;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class FieldMapper {
    /**
     * 右对象的类型
     */
    public String type;
    /**
     * 右对象的字段
     */
    public String srcField;
    /**
     * 左对象目标字段
     */
    public String targetField;

    public FieldMapper(String type, String srcField, String targetField) {
        this.type = type;
        this.srcField = srcField;
        this.targetField = targetField;
    }

    public FieldMapper(String srcField, String targetField) {
        this.srcField = srcField;
        this.targetField = targetField;
    }
}

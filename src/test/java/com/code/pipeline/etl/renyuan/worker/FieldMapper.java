package com.code.pipeline.etl.renyuan.worker;

/**
 * 文件名称: FieldMapper.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/11/4     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
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

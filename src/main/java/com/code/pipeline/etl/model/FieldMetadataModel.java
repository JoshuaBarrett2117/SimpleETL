package com.code.pipeline.etl.model;

/**
 * 文件名称: MetadataModel.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/23     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class FieldMetadataModel {
    /**
     * 字段名称
     */
    private String name;
    /**
     * 字段类型
     */
    private Class type;

    public FieldMetadataModel(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}

package com.code.pipeline.etl.renyuan.worker;

/**
 * 文件名称: WarpFactory.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/11/4     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class WarpFactory {
    public static IFieldWarp create(String value) {
        if (value != null && value.equalsIgnoreCase("车")) {
            return new CphFieldWarp();
        } else {
            return new DefaultFieldWarp();
        }
    }
}

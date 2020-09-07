package com.code.tooltrans.common.source.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved.
 * ====================================================
 * 文件名称: Assert.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/8/26     		liufei(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 */
public final class ExcelFileSourceBuilder {
    protected FileInputStream fileInputStream;
    private List<Integer> sheetIndices;
    private List<String> columnNames;
    private boolean isFirstRowsAreColumns;

    private ExcelFileSourceBuilder(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    private ExcelFileSourceBuilder(String path) {
        try {
            this.fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ExcelFileSourceBuilder(File file) {
        try {
            this.fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExcelFileSourceBuilder anExcelFileSource(File file) {
        return new ExcelFileSourceBuilder(file);
    }

    public static ExcelFileSourceBuilder anExcelFileSource(String path) {
        return new ExcelFileSourceBuilder(path);
    }

    public static ExcelFileSourceBuilder anExcelFileSource(FileInputStream fileInputStream) {
        return new ExcelFileSourceBuilder(fileInputStream);
    }

    public ExcelFileSourceBuilder sheetIndices(List<Integer> sheetIndices) {
        this.sheetIndices = sheetIndices;
        return this;
    }

    public ExcelFileSourceBuilder isFirstRowsAreColumns(boolean isFirstRowsAreColumns) {
        this.isFirstRowsAreColumns = isFirstRowsAreColumns;
        return this;
    }

    public ExcelFileSourceBuilder fileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
        return this;
    }

    public ExcelFileSourceBuilder columnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public ExcelFileSource build() {
        return new ExcelFileSource(fileInputStream, sheetIndices, columnNames, isFirstRowsAreColumns);
    }
}

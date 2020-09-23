package com.code.tooltrans.common.source.excel;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 10:23
 */
public class ExcelFileSource implements IDataSource {
    protected FileInputStream fileInputStream;
    private List<Integer> sheetIndices;
    /**
     * 第一行是否为列名
     */
    private boolean isFirstRowsAreColumns;

    private List<String[]> datas;

    private List<String> columnNames;

    public ExcelFileSource(FileInputStream fileInputStream, List<Integer> sheetIndices, List<String> columnNames, boolean isFirstRowsAreColumns) {
        this.fileInputStream = fileInputStream;
        this.sheetIndices = sheetIndices;
        this.isFirstRowsAreColumns = isFirstRowsAreColumns;
        this.columnNames = columnNames;
    }

    public ExcelFileSource(String path, List<Integer> sheetIndices, List<String> columnNames, boolean isFirstRowsAreColumns) {
        try {
            this.fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.sheetIndices = sheetIndices;
        this.isFirstRowsAreColumns = isFirstRowsAreColumns;
        this.columnNames = columnNames;
    }

    public ExcelFileSource(File file, List<Integer> sheetIndices, List<String> columnNames, boolean isFirstRowsAreColumns) {
        try {
            this.fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.sheetIndices = sheetIndices;
        this.isFirstRowsAreColumns = isFirstRowsAreColumns;
        this.columnNames = columnNames;
    }

    private void parse(int index) {
        ExcelParser parse = null;
        try {
            parse = new ExcelParser().parse(this.fileInputStream, index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        datas = parse.getDatas(false);
    }

    @Override
    public DataRowModel queryForObject(Exp sql) {
        return null;
    }

    @Override
    public Iterator<DataRowModel> iterator(Exp sql) {
        Iterator<Integer> indexIterator = this.sheetIndices.iterator();
        if (indexIterator.hasNext()) {
            String[] columnsName = new String[0];
            parse(indexIterator.next());
            if (isFirstRowsAreColumns && datas.size() > 0) {
                columnsName = datas.remove(0);
            }
            Iterator<String[]> dataIterator = datas.iterator();
            return new ResultIterator(indexIterator, dataIterator, columnsName, columnNames);
        } else {
            return Collections.emptyIterator();
        }

    }

    class ResultIterator implements Iterator<DataRowModel> {
        Iterator<Integer> indexIterator;
        Iterator<String[]> dataIterator;
        String[] columnsName;
        List<String> whereNames;

        public ResultIterator(Iterator<Integer> indexIterator, Iterator<String[]> dataIterator, String[] columnsName, List<String> whereNames) {
            this.indexIterator = indexIterator;
            this.dataIterator = dataIterator;
            this.columnsName = columnsName;
            this.whereNames = whereNames;
        }

        @Override
        public boolean hasNext() {
            if (indexIterator.hasNext() == false && dataIterator.hasNext() == false) {
                return false;
            }
            if (dataIterator.hasNext() == true || indexIterator.hasNext() == true) {
                return true;
            }
            return false;
        }

        @Override
        public DataRowModel next() {
            String[] next;
            if (dataIterator.hasNext()) {
                next = dataIterator.next();
            } else {
                Integer next1 = indexIterator.next();
                parse(next1);
                dataIterator = datas.iterator();
                next = dataIterator.next();
            }
            DataRowModel d = new DataRowModel();
            for (int i = 0; i < next.length; i++) {
                if (whereNames != null && whereNames.size() > 0) {
                    if (whereNames.contains(columnsName[i])) {
                        d.addProperties(columnsName[i], next[i]);
                    }
                } else {
                    if (columnsName.length != 0 && columnsName.length == next.length) {
                        d.addProperties(columnsName[i], next[i]);
                    } else {
                        d.addProperties(String.valueOf(i), next[i]);
                    }
                }
            }
            return d;
        }
    }

}

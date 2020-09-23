package com.code.tooltrans.common.target.excel;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ClassName: SXSSFTest
 *
 * @author qiaoshuai
 * @Description: TODO
 */
public class ExcelTarget implements IDataTarget {
    private List<String> columns;
    private FileOutputStream fileOutputStream;
    private SXSSFWorkbook wb;
    private Sheet sh;
    private AtomicLong rowNum = new AtomicLong();

    private Boolean isOutHead = false;

    public ExcelTarget(FileOutputStream fileOutputStream, List<String> columns) {
        this.columns = columns;
        this.fileOutputStream = fileOutputStream;
        this.init();
    }

    public ExcelTarget(String path, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.init();
    }

    public ExcelTarget(File file, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.init();
    }

    private void init() {
        wb = new SXSSFWorkbook();
        sh = wb.createSheet();
    }

    @Override
    public boolean save(List<DataRowModel> docs, String indexName) {
        if (!isOutHead) {
            synchronized (isOutHead) {
                if (!isOutHead) {
                    Row row = sh.createRow(0);
                    for (int i = 0; i < columns.size(); i++) {
                        Cell cell = row.createCell(i);
                        Object o = columns.get(i);
                        cell.setCellValue(o == null ? "" : o.toString());
                    }
                    isOutHead = true;
                    this.rowNum.incrementAndGet();
                } else {
                    createRow(docs);
                }
            }
        } else {
            createRow(docs);
        }
        return true;
    }

    private void createRow(List<DataRowModel> docs) {
        for (int rowNum = 0; rowNum < docs.size(); rowNum++) {
            DataRowModel dataRowModel = docs.get(rowNum);
            Row row = sh.createRow((int) this.rowNum.get());
            for (int cellNum = 0; cellNum < columns.size(); cellNum++) {
                Cell cell = row.createCell(cellNum);
                Object o = dataRowModel.get(columns.get(cellNum));
                cell.setCellValue(o == null ? "" : o.toString());
            }
            sh.getRow(rowNum);
            this.rowNum.incrementAndGet();
        }
    }

    @Override
    public boolean close() {
        try {
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("关闭流失败");
        }
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
        throw new RuntimeException("暂不支持");
    }
}

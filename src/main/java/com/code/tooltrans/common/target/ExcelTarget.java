package com.code.tooltrans.common.target;

import com.code.common.dao.core.model.DomainElement;
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

/**
 * ClassName: SXSSFTest
 *
 * @author qiaoshuai
 * @Description: TODO
 */
public class ExcelTarget implements IDataTarget {
    private List<String> columns;
    private FileOutputStream fileOutputStream;

    public ExcelTarget(FileOutputStream fileOutputStream, List<String> columns) {
        this.columns = columns;
        this.fileOutputStream = fileOutputStream;
    }

    public ExcelTarget(String path, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ExcelTarget(File file, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(List<DomainElement> docs, String indexName) {
        try {
            // 创建基于stream的工作薄对象的
            SXSSFWorkbook wb = new SXSSFWorkbook(docs.size());
            Sheet sh = wb.createSheet();
            for (int rowNum = 0; rowNum < docs.size(); rowNum++) {
                DomainElement domainElement = docs.get(rowNum);
                Row row = sh.createRow(rowNum);
                for (int cellNum = 0; cellNum < columns.size(); cellNum++) {
                    Cell cell = row.createCell(cellNum);
                    cell.setCellValue(domainElement.getProperties().getOrDefault(columns.get(cellNum), "").toString());
                }
                sh.getRow(rowNum);
            }
            wb.write(fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean close() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("关闭流失败");
        }
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DomainElement> docs, String indexName) {
        throw new RuntimeException("暂不支持");
    }
}

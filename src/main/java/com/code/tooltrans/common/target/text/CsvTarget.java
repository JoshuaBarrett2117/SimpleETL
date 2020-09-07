package com.code.tooltrans.common.target.text;

import com.code.common.dao.core.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.tooltrans.common.IDataTarget;

import java.io.*;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 10:35
 */
public class CsvTarget implements IDataTarget {
    private FileOutputStream fileOutputStream;
    private List<String> columns;
    /**
     * The name of a supported
     * {@link java.nio.charset.Charset charset}
     */
    private String charsetName;
    private Boolean isOutHead = false;

    public CsvTarget(FileOutputStream fileOutputStream, List<String> columns) {
        this.columns = columns;
        this.fileOutputStream = fileOutputStream;
        this.init();
    }

    public CsvTarget(String path, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.init();
    }

    public CsvTarget(File file, List<String> columns) {
        this.columns = columns;
        try {
            this.fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.init();
    }

    private void init() {
        byte[] uft8bom = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
        try {
            fileOutputStream.write(uft8bom);
        } catch (IOException e) {
            throw new RuntimeException("写入bom失败");
        }
    }

    @Override
    public boolean save(List<DomainElement> docs, String indexName) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StringUtils.isNotBlank(charsetName) ? charsetName : "utf-8"));
            if (!isOutHead) {
                synchronized (isOutHead) {
                    if (!isOutHead) {
                        for (int i = 0; i < columns.size(); i++) {
                            bufferedWriter.write(columns.get(i));
                            if (i < columns.size() - 1) {
                                bufferedWriter.write(",");
                            }
                        }
                        bufferedWriter.write("\r\n");
                        isOutHead = true;
                    } else {
                        createRow(docs, bufferedWriter);
                    }
                }
            } else {
                createRow(docs, bufferedWriter);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            try {
                bufferedWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        }
        return true;
    }

    private void createRow(List<DomainElement> docs, BufferedWriter bufferedWriter) throws IOException {
        for (int i = 0; i < docs.size(); i++) {
            DomainElement doc = docs.get(i);
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                Object o = doc.get(column);
                bufferedWriter.write(o == null ? "" : o.toString().replace(",", "，"));
                if (j < columns.size() - 1) {
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.write("\r\n");
        }
    }

    @Override
    public boolean saveOrUpdate(List<DomainElement> docs, String indexName) {
        throw new RuntimeException("暂不支持");
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

    /**
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset charset}
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}

package com.code.tooltrans.common.target.text;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.utils.StringUtils;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.target.AbstractTarget;

import java.io.*;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 10:35
 */
public class TextFileTarget extends AbstractTarget {
    private FileOutputStream fileOutputStream;
    private String elementKey;
    /**
     * The name of a supported
     * {@link java.nio.charset.Charset charset}
     */
    private String charsetName;

    public TextFileTarget(FileOutputStream fileOutputStream, String elementKey) {
        this.elementKey = elementKey;
        this.fileOutputStream = fileOutputStream;
    }

    public TextFileTarget(String path, String elementKey) {
        this.elementKey = elementKey;
        try {
            this.fileOutputStream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public TextFileTarget(File file, String elementKey) {
        this.elementKey = elementKey;
        try {
            this.fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(List<DataRowModel> docs, String indexName) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StringUtils.isNotBlank(charsetName) ? charsetName : "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < docs.size(); i++) {
            DataRowModel doc = docs.get(i);
            try {
                bufferedWriter.write(doc.get(elementKey).toString());
                bufferedWriter.write("\r\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                try {
                    bufferedWriter.close();
                } catch (IOException ex) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
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

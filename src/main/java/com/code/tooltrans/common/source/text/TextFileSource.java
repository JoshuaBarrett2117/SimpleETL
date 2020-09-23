package com.code.tooltrans.common.source.text;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataSource;

import java.io.*;
import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 10:23
 */
public class TextFileSource implements IDataSource {
    protected FileInputStream fileInputStream;

    public TextFileSource(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public TextFileSource(String path) {
        try {
            this.fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public TextFileSource(File file) {
        try {
            this.fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataRowModel queryForObject(Exp sql) {
        return null;
    }

    @Override
    public Iterator<DataRowModel> iterator(Exp sql) {
        BufferedReader readerSrc = null;
        try {
            readerSrc = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = readerSrc;
        return new Iterator<DataRowModel>() {
            String s;
            boolean isOut = true;

            @Override
            public boolean hasNext() {
                try {
                    if (isOut) {
                        s = reader.readLine();
                    }
                    boolean b = this.s != null;
                    if (!b) {
                        reader.close();
                    } else {
                        isOut = false;
                    }
                    return b;
                } catch (IOException e) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    throw new RuntimeException(e);
                }
            }

            @Override
            public DataRowModel next() {
                DataRowModel dataRowModel = new DataRowModel();
                dataRowModel.addProperties("text", s);
                isOut = true;
                return dataRowModel;
            }
        };
    }
}

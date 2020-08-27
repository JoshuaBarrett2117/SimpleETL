package common.source.text;

import common.IDataSource;
import dao.core.model.DomainElement;

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
    public DomainElement queryForObject(Exp sql) {
        return null;
    }

    @Override
    public Iterator<DomainElement> iterator(IDataSource.Exp sql) {
        BufferedReader readerSrc = null;
        try {
            readerSrc = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = readerSrc;
        return new Iterator<DomainElement>() {
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
            public DomainElement next() {
                DomainElement domainElement = new DomainElement();
                domainElement.addProperties("text", s);
                isOut = true;
                return domainElement;
            }
        };
    }
}

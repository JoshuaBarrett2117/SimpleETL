package common.target;

import common.IDataTarget;
import dao.core.model.DomainElement;
import utils.StringUtils;

import java.io.*;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 10:35
 */
public class TextFileTarget implements IDataTarget {
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
    public boolean save(List<DomainElement> docs, String indexName) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StringUtils.isNotBlank(charsetName) ? charsetName : "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < docs.size(); i++) {
            DomainElement doc = docs.get(i);
            try {
                bufferedWriter.write(doc.get(elementKey).toString());
                bufferedWriter.write("\r\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
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

    @Override
    public boolean saveOrUpdate(List<DomainElement> docs, String indexName) {
        throw new RuntimeException("暂不支持");
    }

    /**
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset charset}
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}

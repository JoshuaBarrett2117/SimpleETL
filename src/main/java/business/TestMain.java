package business;

import business.dlwz.FJSDLWZDataSourceMain;
import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.ElasticsearchTarget;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 8:51
 */
public class TestMain extends AbstractMain {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new TestMain().deal(null, properties.getProperty("es_dict_name"));
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new FileSource(properties.getProperty("dict_txt_file"));
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new IIteratorTranser() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            @Override
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            @Override
                            public DomainElement next() {
                                DomainElement next = iterator.next();
                                return CommonUtil.createDictDoc(next.get("text").toString(), Arrays.asList("I-地理位置-Xiang2Xi4Di4Zhi3"));
                            }
                        };
                    }
                }
        );
    }


}

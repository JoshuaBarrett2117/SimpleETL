package business;

import com.code.common.dao.model.DomainElement;
import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranser;
import common.source.ElasticsearchSource;
import common.target.ElasticsearchTarget;
import common.target.FileTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/11 8:31
 */
public class EsToFileMain extends AbstractMain {
    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("{" +
                "  \"size\": 10000, \n" +
                "  \"_source\": \"sentence_text\"\n" +
                "}");
        exp.addTableName("mature_corpus_plugin002");
        new EsToFileMain().deal(exp, null);
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(new IIteratorTranser() {
            @Override
            public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                return new Iterator<DomainElement>() {
                    int count = 0;

                    @Override
                    public boolean hasNext() {
//                        if (++count > 10) {
//                            return false;
//                        }
                        return iterator.hasNext();
                    }

                    @Override
                    public DomainElement next() {
                        return iterator.next();
                    }
                };
            }
        });
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new FileTarget("C:/Users/joshua/Desktop/文本提取/触发词提取语料.txt", "sentence_text");
    }
}

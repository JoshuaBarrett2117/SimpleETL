package business;

import com.code.common.dao.model.DomainElement;
import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranser;
import common.source.ElasticsearchSource;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class EsToEsMain2 extends AbstractMain {
    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("{" +
                "  \"size\": 5000, \n" +
                "  \"_source\": \"sentence_text\"\n" +
                "}");
        exp.addTableName("mature_corpus_plugin002");
        new EsToEsMain2().deal(exp, "mature_corpus_plugin000");
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
                        if (++count > 10) {
                            return false;
                        }
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
        return new ElasticsearchTarget(properties);
    }
}

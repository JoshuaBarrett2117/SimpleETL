package business.algorithm;

import  dao.core.model.DomainElement;
import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranslator;
import common.source.elasticsearch.ElasticsearchSource;
import common.target.TextFileTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/11 8:31
 */
public class OracleToFileMain extends AbstractMain {
    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("");
        new OracleToFileMain().deal(exp, null);
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(new IIteratorTranslator() {
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
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:/Users/joshua/Desktop/文本提取/触发词提取语料.txt", "sentence_text");
    }
}

package business.kg;

import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:07
 */
public class KgToEsMain2 extends AbstractMain {

    public static void main(String[] args) {
        new KgToEsMain2().deal(null, "frebase_kg_200413");

//        FileSource fileSource = new FileSource("D:\\data\\KnowledgeGraph\\freebase\\freebase-rdf-latest");
//        Iterator<DomainElement> iterator = fileSource.iterator(null);
//        long count = 0;
//        while (iterator.hasNext()) {
//            iterator.next();
//            count++;
//            if (count % 500000 == 0) {
//                System.out.println("当前:" + count);
//            }
//        }
//        System.out.println("总:" + count);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new IIteratorTranser() {
                    long count = 0;

                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            @Override
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            @Override
                            public DomainElement next() {
                                if (count++ < 2819555000L - 85000) {
                                    if (count % 50000 == 0) {
                                        System.out.println(count);
                                    }
                                    iterator.next();
                                    return null;
                                }
                                return iterator.next();
                            }
                        };
                    }
                },
                new KgToEsTranser()
        );
    }


    @Override
    protected IDataSource dataSource(Properties properties) {
        return new FileSource("D:\\data\\KnowledgeGraph\\freebase\\freebase-rdf-latest");
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }
}

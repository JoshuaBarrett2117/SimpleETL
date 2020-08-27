package business.algorithm.kg;

import  dao.core.model.DomainElement;
import common.*;
import common.source.text.TextFileSource;
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
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new IIteratorTranslator() {
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
                new KgToEsTranslator()
        );
    }


    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource("D:\\data\\KnowledgeGraph\\freebase\\freebase-rdf-latest");
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }
}

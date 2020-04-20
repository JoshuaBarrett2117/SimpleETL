package business.kg;

import common.*;
import common.source.FileSource;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:07
 */
public class KgToEsMain extends AbstractMain {

    public static void main(String[] args) {
        new KgToEsMain().deal(null, "frebase_kg_200410");

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
                new KgToEsTranser()
        );
    }

    @Override
    protected IDataSource getDataSource(Properties properties) {
        return new FileSource("D:\\data\\KnowledgeGraph\\freebase\\freebase-rdf-latest");
    }

    @Override
    protected IDataTarget getDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }
}

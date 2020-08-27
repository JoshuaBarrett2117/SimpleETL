package business.algorithm.kg;

import common.*;
import common.source.text.TextFileSource;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:07
 */
public class KgToEsMultithreadMain extends AbstractMultithreadingMain {

    public KgToEsMultithreadMain(int threadCount) {
        super(threadCount);
    }

    public static void main(String[] args) {
        new KgToEsMultithreadMain(20).deal(null, "frebase_kg_200410");
    }

    @Override
    protected List<IIteratorTranslator> getTransers() {
        return Arrays.asList(
                new KgToEsTranslator()
        );
    }

    @Override
    protected IDataSource getDataSource(Properties properties) {
        return new TextFileSource("D:\\data\\KnowledgeGraph\\freebase\\freebase-rdf-latest");
    }

    @Override
    protected IDataTarget getDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }
}

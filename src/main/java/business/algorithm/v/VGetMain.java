package business.algorithm.v;

import common.translator.StringDuplicateRemovalTranslator;
import common.*;
import common.source.elasticsearch.ElasticsearchSource;
import common.target.TextFileTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 熟语料中的动词提取
 *
 * @author by liufei
 * @Description
 * @Date 2020/4/7 13:47
 */
public class VGetMain extends AbstractMain {
    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("{\n" +
                "  \"query\": {\n" +
                "    \"query_string\": {\n" +
                "      \"default_field\": \"label\",\n" +
                "      \"query\": \"NER\",\n" +
                "      \"default_operator\": \"AND\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n");
        exp.setTableNames(Arrays.asList("mature_corpus_plugin007"));
        new VGetMain().deal(exp, null);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new VGetTranslator()
                , new StringDuplicateRemovalTranslator("text")
        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:/Users/joshua/Desktop/文本提取/熟语料动词NER-C查法.csv", "output");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }
}

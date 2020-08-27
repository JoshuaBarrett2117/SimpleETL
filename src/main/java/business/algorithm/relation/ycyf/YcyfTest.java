package business.algorithm.relation.ycyf;

import common.translator.StringSplitTranslator;
import common.*;
import common.source.elasticsearch.ElasticsearchSource;
import common.target.TextFileTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/13 15:46
 */
public class YcyfTest extends AbstractMain {


    public static void main(String[] args) {
        IDataSource.Exp sql = new IDataSource.Exp("{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "}");
        sql.addTableName("mature_corpus_plugin007");
        new YcyfTest().deal(sql, null);
    }


    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new StringSplitTranslator("sentence_text", new YcyfSplitFunc())
        );
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\依存语法提取.txt", "text");
    }
}

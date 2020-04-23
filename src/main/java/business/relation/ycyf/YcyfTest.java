package business.relation.ycyf;

import common.transer.StringSplitTranser;
import common.*;
import common.source.ElasticsearchSource;
import common.target.FileTarget;

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
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new StringSplitTranser("sentence_text", new YcyfSplitFunc())
        );
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new FileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\依存语法提取.txt", "text");
    }
}

package business.algorithm.v;

import  dao.core.model.DomainElement;
import dao.jdbc.operator.Software;
import common.*;
import common.source.elasticsearch.ElasticsearchSource;
import common.target.OracleTarget;
import common.translator.ElementSplitTranslator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/7 18:29
 */
public class VAndVnGetMain extends AbstractMain {
    private List<String> poses = Arrays.asList("v", "vn", "n");

    ElementSplitTranslator.Func func = (element) -> {
        List<DomainElement> result = new ArrayList<>();
        String sentenceText = element.get("sentence_text").toString();
        List<String> tagWordSeq = (List<String>) element.get("tag_word_seq");
        List<String> tagSeq = (List<String>) element.get("tag_seq");
        for (int i = 0; i < tagSeq.size(); i++) {
            String tag = tagSeq.get(i);
            if (poses.contains(tag)) {
                DomainElement de = new DomainElement();
                de.addProperties("SRC_TEXT", sentenceText);
                de.addProperties("WORD", tagWordSeq.get(i));
                de.addProperties("POS", tag);
                result.add(de);
            }
        }
        return result;
    };

    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("{\n" +
                "  \"_source\": [\n" +
                "    \"sentence_text\",\n" +
                "    \"tag_seq\",\n" +
                "    \"tag_word_seq\"\n" +
                "  ]\n" +
                "}");
        exp.setTableNames(Arrays.asList("mature_corpus_plugin002"));
        new VAndVnGetMain().deal(exp, "MATURE_CORPUS_CUT_V_VN");
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new ElementSplitTranslator(func)
        );
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new OracleTarget(connection, software);
    }

    private RdbDataSource rdbDataSource;

    private Software getSoftware() {
        Software software = new Software();
        software.setCode("oracle");
        return software;
    }

    private Connection getConnection(Properties properties) {
        if (rdbDataSource == null) {
            rdbDataSource = new RdbDataSource(properties);
        }
        return rdbDataSource.getConnection();
    }


}

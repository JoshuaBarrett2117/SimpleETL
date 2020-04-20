package business.wordstat;

import common.transer.ElementSplitTranser;
import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.ElasticsearchSource;
import common.target.OracleTarget;

import java.sql.Connection;
import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/14 16:33
 */
public class WordStatMain extends AbstractMain {

    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("{\n" +
                "  \"size\":5000,\n" +
                "  \"query\": {\n" +
                "    \"term\": {\n" +
                "      \"is_ner\": {\n" +
                "        \"value\":true\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");

        exp.addTableName("mature_corpus_plugin003");
        new WordStatMain().deal(exp, "FC_TEST6");
    }

    private List<String> posList = Arrays.asList(
//            "I-地理位置-Xiang2Xi4Di4Zhi3",
            "I-组织-Gong1Si1Ming2Cheng1"
//            ,"I-人员-Xing4Ming2"
    );

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(new ElementSplitTranser(new ElementSplitTranser.Func() {
            @Override
            public List<DomainElement> split(DomainElement content) {
                return createDocs(content);
            }
        }));
    }

    private List<DomainElement> createDocs(DomainElement next1) {
        List<DomainElement> domainElements = new ArrayList<>();
        List<String> tagSeq = (List<String>) next1.get("tag_seq");
        List<String> tagWordSeq = (List<String>) next1.get("tag_word_seq");
        if (tagSeq.size() != tagWordSeq.size()) {
            return null;
        }
        String id = next1.getId();
        for (int i = 0; i < tagSeq.size(); i++) {
            String pos = tagSeq.get(i);
            if (posList.contains(pos)) {
                DomainElement doc = createDoc(tagSeq, tagWordSeq, i);
                doc.addProperties("DOC_ID", id);
                domainElements.add(doc);
            }
        }
        return domainElements;
    }

    private DomainElement createDoc(List<String> tagSeq, List<String> tagWordSeq, int i) {
        DomainElement de = new DomainElement();
        de.addProperties("A1", (i - 1) < 0 ? null : tagWordSeq.get(i - 1));
        de.addProperties("A2", (i - 2) < 0 ? null : tagWordSeq.get(i - 2));
        de.addProperties("A3", (i - 3) < 0 ? null : tagWordSeq.get(i - 3));
        de.addProperties("A4", (i - 4) < 0 ? null : tagWordSeq.get(i - 4));
        de.addProperties("F1", (i + 1) >= tagSeq.size() ? null : tagWordSeq.get(i + 1));
        de.addProperties("F2", (i + 2) >= tagSeq.size() ? null : tagWordSeq.get(i + 2));
        de.addProperties("F3", (i + 3) >= tagSeq.size() ? null : tagWordSeq.get(i + 3));
        de.addProperties("F4", (i + 4) >= tagSeq.size() ? null : tagWordSeq.get(i + 4));
        de.addProperties("A1_POS", (i - 1) < 0 ? null : tagSeq.get(i - 1));
        de.addProperties("A2_POS", (i - 2) < 0 ? null : tagSeq.get(i - 2));
        de.addProperties("A3_POS", (i - 3) < 0 ? null : tagSeq.get(i - 3));
        de.addProperties("A4_POS", (i - 4) < 0 ? null : tagSeq.get(i - 4));
        de.addProperties("F1_POS", (i + 1) >= tagSeq.size() ? null : tagSeq.get(i + 1));
        de.addProperties("F2_POS", (i + 2) >= tagSeq.size() ? null : tagSeq.get(i + 2));
        de.addProperties("F3_POS", (i + 3) >= tagSeq.size() ? null : tagSeq.get(i + 3));
        de.addProperties("F4_POS", (i + 4) >= tagSeq.size() ? null : tagSeq.get(i + 4));
        de.addProperties("HEAD_WORD", tagWordSeq.get(i));
        de.addProperties("HEAD_WORD_POST", tagSeq.get(i));
        de.addProperties("INSERT_TIME", new Date());
        return de;
    }

    @Override
    protected IDataSource getDataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected IDataTarget getDataTarget(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        return new OracleTarget(connection, software);
    }
}

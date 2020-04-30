package business.company;

import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.*;
import common.source.ElasticsearchSource;
import common.target.OracleTarget;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/24 9:43
 */
public class CompanyCutWordNatureMain extends AbstractMain {

    private static final String SOURCE_TABLE = "monistic_core_words_2";
    private static final String TARGET_TABLE = "COMPANY_PATTERN_GROUP_BY";
    private static Segment segment;

    static {
        segment = HanLP.newSegment();
        segment.enableCustomDictionary(false);
    }

    public static void main(String[] args) {
        IDataSource.Exp sourceSql = new IDataSource.Exp("{\n" +
                "  \"size\": 5000, \n" +
                "  \"query\": {\n" +
                "    \"term\": {\n" +
                "      \"tag_seq.keyword\": {\n" +
                "        \"value\": \"I-组织-Gong1Si1Ming2Cheng1\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": \"word\"\n" +
                "}");
        sourceSql.addTableName(SOURCE_TABLE);
        new CompanyCutWordNatureMain().deal(sourceSql, TARGET_TABLE);
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new ElasticsearchSource(properties);
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new OracleTarget(connection, software);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new IIteratorTranser() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            @Override
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            @Override
                            public DomainElement next() {
                                DomainElement next = iterator.next();
                                String word = next.get("word").toString();
                                List<Term> segs = segment.seg(word);
                                DomainElement n = new DomainElement();
                                StringBuilder sb = new StringBuilder();
                                for (Term seg : segs) {
                                    if (sb.length() > 0) {
                                        sb.append("-");
                                    }
                                    sb.append(seg.nature.toString());
                                }
                                n.addProperties("PATTERN_GROUP", sb.toString());
                                n.addProperties("SRC_TEXT", word);
                                return n;
                            }
                        };
                    }
                }
        );
    }
}

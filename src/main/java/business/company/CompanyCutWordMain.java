package business.company;

import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.*;
import common.source.ElasticsearchSource;
import common.target.OracleTarget;
import common.transer.ElementSplitTranser;

import java.sql.Connection;
import java.util.*;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/24 9:43
 */
public class CompanyCutWordMain extends AbstractMain {

    private static final String SOURCE_TABLE = "instance_jin1rong2ling3yu4cao2_zu3zhi1";
    private static final String TARGET_TABLE = "COMPANY_WORD_PATTERN_GROUP_BY";
    private static Segment segment;

    static {
        segment = HanLP.newSegment();
        segment.enableCustomDictionary(false);
    }

    public static void main(String[] args) {
        IDataSource.Exp sourceSql = new IDataSource.Exp("{\n" +
                "  \"size\": 5000,\n" +
                "  \"query\": {\n" +
                "    \"exists\": {\n" +
                "      \"field\": \"Gong1Si1Ming2Cheng1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": \"Gong1Si1Ming2Cheng1\"\n" +
                "}");
        sourceSql.addTableName(SOURCE_TABLE);
        new CompanyCutWordMain().deal(sourceSql, TARGET_TABLE);
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        properties.setProperty("esIp","192.168.125.5");
        properties.setProperty("esPort","9400");
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
                new ElementSplitTranser(new ElementSplitTranser.Func() {
                    @Override
                    public List<DomainElement> split(DomainElement content) {
                        String word = content.get("Gong1Si1Ming2Cheng1").toString();
                        List<Term> segs = segment.seg(word);
                        List<DomainElement> des = new ArrayList<>();
                        for (Term seg : segs) {
                            DomainElement de = new DomainElement();
                            de.addProperties("SRC_TEXT", word);
                            de.addProperties("WORD", seg.word);
                            des.add(de);
                        }
                        return des;
                    }
                })
        );
    }
}

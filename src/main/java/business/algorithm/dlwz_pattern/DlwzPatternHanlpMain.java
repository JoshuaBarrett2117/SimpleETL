package business.algorithm.dlwz_pattern;

import  dao.core.model.DomainElement;
import dao.jdbc.operator.Software;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.*;
import common.source.rdb.RdbSource;
import common.target.OracleTarget;

import java.sql.Connection;
import java.util.*;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/22 14:26
 */
public class DlwzPatternHanlpMain extends AbstractMain {
    private static Segment segment;
    private RdbDataSource rdbDataSource;

    static {
        segment = HanLP.newSegment();
        segment.enableCustomDictionary(true);
    }

    public static void main(String[] args) {
        new DlwzPatternHanlpMain().deal(new IDataSource.Exp("select address from PY_AMAP_LBS_INFO where address!='[]'"),
                "DLWZ_PATTERN_GROUP_BY2");
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new IIteratorTranslator() {
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
                                String name = next.get("ADDRESS").toString();
                                List<Term> seg = segment.seg(name);
                                StringBuilder sb = new StringBuilder();
                                for (Term term : seg) {
                                    if (sb.length() != 0) {
                                        sb.append("-");
                                    }
                                    sb.append(term.nature);
                                }
                                DomainElement de = new DomainElement();
                                de.addProperties("PATTERN_GROUP", sb.toString());
                                de.addProperties("SRC_TEXT", name);
                                return de;
                            }
                        };
                    }
                }
        );
    }


    @Override
    protected IDataSource buildDataSource(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new RdbSource(connection, software);
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new OracleTarget(connection, software);
    }

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

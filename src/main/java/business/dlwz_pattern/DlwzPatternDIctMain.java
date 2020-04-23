package business.dlwz_pattern;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.FileTarget;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/22 14:14
 */
public class DlwzPatternDIctMain extends AbstractMain {

    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("SELECT NAME,TYPE FROM DLWZ_ROAD_V WHERE TYPE != '详细地址'");
        new DlwzPatternDIctMain().deal(exp, null);
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
                                String name = next.get("NAME").toString().trim();
                                if (StringUtils.isBlank(name) || name.contains(" ")) {
                                    return null;
                                }
                                String type = next.get("TYPE").toString();
                                next.addProperties("text", name + " " + type + " " + 3000);
                                return next;
                            }
                        };
                    }
                }
        );
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new OracleSource(connection, software);
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new FileTarget("C:\\hanlp\\data\\dictionary\\custom\\地址位置词典.txt", "text");
    }

}

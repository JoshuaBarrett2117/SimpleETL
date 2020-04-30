package business.dlwz_pattern;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.FileTarget;

import java.sql.Connection;
import java.util.*;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/22 14:14
 */
public class DlwzPatternDIctMain extends AbstractMain {

    public static void main(String[] args) {
        IDataSource.Exp exp = new IDataSource.Exp("SELECT NAME,TYPE FROM DLWZ_ROAD_V_2 WHERE TYPE != '详细地址' and TYPE!= '具体地点'");
        new DlwzPatternDIctMain().deal(exp, null);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new IIteratorTranser() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            Iterator<DomainElement> iter = iterator;
                            List<String> list = Arrays.asList(
                                    "号 号 503676",
                                    "路 路 268362",
                                    "米 米 124939",
                                    "附近 附近 103313",
                                    "- 杆 91607",
                                    "与 与 89776",
                                    "交叉口 交叉口 86524",
                                    "楼 楼 73720",
                                    "街 街 56172",
                                    "层 层 39850",
                                    "村 村 28697",
                                    "大道 大道 25952",
                                    "店面 店面 23976",
                                    "幢 幢 23276",
                                    "栋 栋 23032",
                                    "店 店 21403",
                                    "城 城 20221",
                                    "广场 广场 20094",
                                    "大厦 大厦 18695",
                                    "里 里 14785 ",
                                    "道 道 14206");

                            @Override
                            public boolean hasNext() {
                                boolean b = iter.hasNext();
                                if (!b && list != null) {
                                    List<DomainElement> domainElements = new ArrayList<>();
                                    for (String s : list) {
                                        String[] split = s.split("\\s");
                                        DomainElement de = new DomainElement();
                                        de.addProperties("NAME", split[0]);
                                        de.addProperties("TYPE", split[1]);
                                        de.addProperties("NUM", split[2]);
                                        domainElements.add(de);
                                    }
                                    iter = domainElements.iterator();
                                    list = null;
                                }
                                return iter.hasNext();
                            }

                            @Override
                            public DomainElement next() {
                                DomainElement next = iter.next();
                                String name = next.get("NAME").toString().trim();
                                if (StringUtils.isBlank(name) || name.contains(" ")) {
                                    return null;
                                }
                                String type = next.get("TYPE").toString();
                                String num = next.get("NUM") == null ? "3000" : next.get("NUM").toString();
                                next.addProperties("text", name + " " + type + " " + num);
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

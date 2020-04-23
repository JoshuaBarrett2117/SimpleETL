package business.dlwz;

import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.OracleTarget;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 完整的地址切词（包含行政区划）
 *
 * @author liufei
 * @Description
 * @Date 2020/4/20 10:27
 */
public class FJSDlwzCompleteCutWordMain extends AbstractMain {

    private RdbDataSource rdbDataSource;
    private static final String key = "TEXT";

    public static void main(String[] args) {
//        Properties properties = new Properties();
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(
//                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
//                    Charset.forName("GBK"));
//            properties.load(inputStreamReader);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        IDataSource.Exp exp = new IDataSource.Exp(
                "select concat(concat(concat(concat(PNAME,CITYNAME),ADNAME),ADDRESS),NAME) as " + FJSDlwzCompleteCutWordMain.key + "  from PY_AMAP_LBS_INFO where ADDRESS !='[]'"
        );
        new FJSDlwzCompleteCutWordMain().deal(exp, "COMPLETE_DLWZ_FC_TEST");
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new OracleSource(connection, software);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new DlwzHanLpTranser(FJSDlwzCompleteCutWordMain.key)
        );
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new OracleTarget(connection, software);
    }

    @NotNull
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

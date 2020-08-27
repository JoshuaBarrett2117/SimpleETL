package business.algorithm.dlwz;

import dao.jdbc.operator.Software;
import common.*;
import common.source.rdb.RdbSource;
import common.target.OracleTarget;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/20 10:27
 */
public class FJSDlwzCutWordMain extends AbstractMain {

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
                "select DISTINCT ADDRESS AS " + FJSDlwzCutWordMain.key + " from PY_AMAP_LBS_INFO where ADDRESS !='[]' "
        );
        new FJSDlwzCutWordMain().deal(exp, "DLWZ_FC_TEST");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        Connection connection = getConnection(properties);
        Software software = getSoftware();
        //输入源
        return new RdbSource(connection, software);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new DlwzHanLpTranslator(FJSDlwzCutWordMain.key)
        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
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

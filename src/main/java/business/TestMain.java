package business;

import business.dlwz.FJSDLWZDataSourceMain;
import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.FileSource;
import common.source.OracleSource;
import common.target.ElasticsearchTarget;
import common.target.FileTarget;
import common.transer.StringDuplicateRemovalTranser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 8:51
 */
public class TestMain extends AbstractMain {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new TestMain().deal(new IDataSource.Exp("SELECT ADDRESS FROM PY_AMAP_LBS_INFO WHERE ADDRESS !='[]' "), null);
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
        return new FileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\测试流程.txt", "ADDRESS");
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new StringDuplicateRemovalTranser("ADDRESS")
        );
    }


}

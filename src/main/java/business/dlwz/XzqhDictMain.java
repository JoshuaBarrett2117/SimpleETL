package business.dlwz;

import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.FileTarget;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/21 9:14
 */
public class XzqhDictMain extends AbstractMain {

    public static void main(String[] args) {
        new XzqhDictMain().deal(new IDataSource.Exp("select mc from BM_STATS2018_QHYCXDM"), null);
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
        return new FileTarget("C:\\hanlp\\data\\dictionary\\custom\\行政区划自定义词典.txt", "MC");
    }

}

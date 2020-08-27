package business.algorithm.dlwz;

import dao.jdbc.operator.Software;
import common.*;
import common.source.rdb.RdbSource;
import common.target.TextFileTarget;
import common.translator.StringDuplicateRemovalTranslator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;

/**
 * 福建省地址细拆分
 *
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:02
 */
public class FJSDLWZExportMain extends AbstractMain {


    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZExportMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IDataSource.Exp exp = new IDataSource.Exp("SELECT concat(pname,concat(cityname,concat(adname,address))) as name FROM PY_AMAP_LBS_INFO_GRID_35 where ADDRESS !='[]' ");
        new FJSDLWZExportMain().deal(exp, null);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                 new StringDuplicateRemovalTranslator("NAME")
        );
    }

    @Override
    public IDataTarget buildDataTarget(Properties properties) {
//        return new ElasticsearchTarget(properties);
        return new TextFileTarget("C:/Users/joshua/Desktop/文本提取/福建省完整地址.txt", "NAME");
    }

    @Override
    public IDataSource buildDataSource(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new RdbSource(connection, software);
    }
}

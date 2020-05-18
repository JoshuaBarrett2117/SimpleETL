package business.dlwz;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.FileTarget;
import common.transer.ConditionDeleteTranser;
import common.transer.StringDuplicateRemovalTranser;
import common.transer.StringSplitTranser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                 new StringDuplicateRemovalTranser("NAME")
        );
    }

    @Override
    public IDataTarget dataTarget(Properties properties) {
//        return new ElasticsearchTarget(properties);
        return new FileTarget("C:/Users/joshua/Desktop/文本提取/福建省完整地址.txt", "NAME");
    }

    @Override
    public IDataSource dataSource(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new OracleSource(connection, software);
    }
}

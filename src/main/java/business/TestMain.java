package business;

import business.dlwz.FJSDLWZDataSourceMain;
import business.v.VAndVnGetMain;
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
public class TestMain {

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

        IDataSource.Exp exp = new IDataSource.Exp("{\n" +
                "  \"_source\": [\n" +
                "    \"sentence_text\",\n" +
                "    \"tag_seq\",\n" +
                "    \"tag_word_seq\"\n" +
                "  ]\n" +
                "}");
        exp.setTableNames(Arrays.asList(properties.getProperty("es_dict_name")));
        new VAndVnGetMain().deal(exp, properties.getProperty("oracle_target"));
    }


}

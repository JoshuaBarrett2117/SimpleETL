package business;

import business.algorithm.dlwz.FJSDLWZDataSourceMain;
import business.algorithm.v.VAndVnGetMain;
import common.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
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

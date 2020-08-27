package business.algorithm.dlwz;

import common.translator.StringDuplicateRemovalTranslator;
import common.translator.StringSplitTranslator;
import  dao.core.model.DomainElement;
import dao.jdbc.operator.Software;
import common.*;
import common.source.text.TextFileSource;
import common.source.rdb.RdbSource;
import common.target.ElasticsearchTarget;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:02
 */
public class DLWZDataSourceMain extends AbstractMain {

    private StringSplitTranslator.Func func1 = new StringSplitTranslator.Func() {
        @Override
        public List<String> split(String content) {
            Set<String> contents = new HashSet<>();
            contents.add(content);
            // 尾部匹配
            if (content.endsWith("社区居委会") && content.length() > 5) {
                String tempContent = content.substring(0, content.length() - 5);
                if (tempContent.length() <= 2) {
                    tempContent = tempContent + "社区";
                }
                contents.add(tempContent);
            }
            return new ArrayList<>(contents);
        }
    };

    private StringSplitTranslator.Func func2 = new StringSplitTranslator.Func() {
        private void readFileContent(File file, List<String> contents) {
            IDataSource dataSource = new TextFileSource(file);
            Iterator<DomainElement> iterator = dataSource.iterator(null);
            while (iterator.hasNext()) {
                contents.add(iterator.next().get("text").toString());
            }
        }

        @Override
        public List<String> split(String content) {
            Set<String> contents = new HashSet<>();
            contents.add(content);

            // 尾部匹配
            String matchCompany = "";
            if (content.endsWith("股份有限公司") && content.length() > 6) {
                contents.add(content.substring(0, content.length() - 6));
                matchCompany = "股份有限公司";
            } else if (content.endsWith("有限责任公司") && content.length() > 6) {
                contents.add(content.substring(0, content.length() - 6));
                matchCompany = "有限责任公司";
            } else if (content.endsWith("有限公司") && content.length() > 4) {
                contents.add(content.substring(0, content.length() - 4));
                matchCompany = "有限公司";
            }

            // 首部匹配  国家统计局  http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html
            // http://www.hotelaah.com/ipo/index.html?d=1585123538693
            List<String> provinceList = new ArrayList<>();
            String filePath = "/province.txt";
            readFileContent(new File(filePath), provinceList);
            String savePath = "/city.txt";
            List<String> cityList = new ArrayList<>();
            readFileContent(new File(savePath), cityList);
            provinceList.addAll(cityList);

            String matchProvince = "";
            for (String province : provinceList) {
                if (content.startsWith(province) && province.length() > matchProvince.length()) {
                    matchProvince = province;
                }
            }
            if (matchProvince.length() >= 1) {
                contents.add(content.substring(matchProvince.length()));
            }

            // 首尾匹配
            if (matchProvince.length() >= 1 && matchCompany.length() >= 1 && matchProvince.length() + matchCompany.length() < content.length()) {
                contents.add(content.substring(matchProvince.length(), content.length() - matchCompany.length()));
            }
            return new ArrayList<>(contents);
        }
    };

    public static void main(String[] args) throws IOException {
        IDataSource.Exp exp = new IDataSource.Exp("select DISTINCT MC from \n" +
                "(\n" +
                "select DISTINCT MC as MC from BM_STATS2018_QHYCXDM\n" +
                "UNION\n" +
                "select DISTINCT XZQH_XXDZ as MC from BM_STATS2018_QHYCXDM )");
        new DLWZDataSourceMain().deal(exp, "monistic_core_words_2");
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new StringSplitTranslator("MC", func1, func2)
                , new XZQHTransIterator()
                , new StringDuplicateRemovalTranslator("word")
        );
    }

    @Override
    public IDataTarget buildDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
        //        return new FileTarget( "C:/Users/joshua/Desktop/文本提取/xzqh.txt", "word");
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

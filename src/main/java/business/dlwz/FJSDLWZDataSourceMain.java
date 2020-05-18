package business.dlwz;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.ElasticsearchTarget;
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
public class FJSDLWZDataSourceMain extends AbstractMain {


    /**
     * 将括号内的提取出来作为一个新词，1个词变成两个词
     */
    private static final Pattern khPattern = Pattern.compile("\\(.{0,}\\)");

    /**
     * 匹配到之后清空匹配到的内容
     */
    private static final Pattern numberPattern = Pattern.compile("^[0-9]+$");


    /**
     * 删除括号内容,然后新增一条括号内容的数据
     */
    private static final StringSplitTranser.Func splitFunc0 = new StringSplitTranser.Func() {
        @Override
        public List<String> split(String content) {
            Set<String> set = new HashSet<>();
            Matcher matcher = khPattern.matcher(content);
            while (matcher.find()) {
                String group = matcher.group();
                set.add(group.replace("(", "").replace(")", ""));
            }
            set.add(matcher.replaceAll("").trim());

            return new ArrayList<>(set);
        }
    };

    private static final StringSplitTranser.Func splitFunc1 = new StringSplitTranser.Func() {
        @Override
        public List<String> split(String content) {
            Set<String> set = new HashSet<>();
            set.add(content);
            //拆分
            for (Pattern splitP : StaticPattern.splitPs) {
                String[] split = splitP.split(content);
                if (split.length > 1) {
                    set.addAll(Arrays.asList(split));
                }
            }
            return new ArrayList<>(set);
        }
    };

    /**
     * 拆分，过滤
     */
    private static final StringSplitTranser.Func splitFunc2 = new StringSplitTranser.Func() {
        @Override
        public List<String> split(String content) {
            Set<String> set = new HashSet<>();
            set.add(content);
            //拆分
            for (Pattern splitP : StaticPattern.splitPs) {
                String[] split = splitP.split(content);
                if (split.length > 1) {
                    set.addAll(Arrays.asList(split));
                }
            }
            //根据正则过滤掉词之后，生成新词
            for (Pattern p : StaticPattern.matchPs) {
                Matcher matcher = p.matcher(content);
                String s = matcher.replaceAll("").trim();
                if (StringUtils.isNotBlank(s)) {
                    set.add(s);
                }
            }
            return new ArrayList<>(set);
        }
    };
    /**
     * 包含分号的删除，过滤比如   105路;108路;118路;134路;305路    这类ADDRESS，这类应该是公交车站
     */
    private static final ConditionDeleteTranser.Condition condition1 = new ConditionDeleteTranser.Condition() {
        @Override
        public boolean isFilter(DomainElement domainElement, String key) {
            String name = domainElement.get(key).toString();
            if (name.contains(";")) {
                return true;
            }
            return false;
        }
    };

    /**
     * 删除长度小于2，或者全数字的词，或者字符/数字开头的词
     */
    private static final ConditionDeleteTranser.Condition condition2 = new ConditionDeleteTranser.Condition() {
        @Override
        public boolean isFilter(DomainElement domainElement, String key) {
            String name = domainElement.get(key).toString();
            Matcher matcher = numberPattern.matcher(name);
            Matcher matcher1 = StaticPattern.numberMatchPattern.matcher(name);
            String trim1 = matcher.replaceAll("").trim();
            String trim2 = matcher1.replaceAll("").trim();
            if (name.length() <= 2 || StringUtils.isBlank(trim1) || StringUtils.isBlank(trim2)) {
                return true;
            }
            return false;
        }
    };

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IDataSource.Exp exp = new IDataSource.Exp("SELECT concat(pname,concat(cityname,concat(adname,address))) FROM PY_AMAP_LBS_INFO where ADDRESS !='[]' ");
        new FJSDLWZDataSourceMain().deal(exp, properties.getProperty("es_dict_name"));
//        new FJSDLWZDataSourceMain().deal(exp, null);
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new ConditionDeleteTranser("NAME", condition1)
                , new StringSplitTranser("NAME", splitFunc0)
                , new StringSplitTranser("NAME", splitFunc1)
                , new StringSplitTranser("NAME", splitFunc2)
//                , new XMDlwzTransIterator()  //创建词典文档的
                , new ConditionDeleteTranser("NAME", condition2)
                , new StringDuplicateRemovalTranser("NAME")
        );
    }

    @Override
    public IDataTarget dataTarget(Properties properties) {
//        return new ElasticsearchTarget(properties);
        return new FileTarget("C:/Users/joshua/Desktop/文本提取/福建省地理位置3.txt", "NAME");
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

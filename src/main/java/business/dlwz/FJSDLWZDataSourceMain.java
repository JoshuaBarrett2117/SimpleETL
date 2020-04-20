package business.dlwz;

import common.transer.ConditionDeleteTranser;
import common.transer.StringDuplicateRemovalTranser;
import common.transer.StringSplitTranser;
import com.code.common.dao.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.metadata.base.softwaredeployment.Software;
import common.*;
import common.source.OracleSource;
import common.target.ElasticsearchTarget;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:02
 */
public class FJSDLWZDataSourceMain extends AbstractMain {
    /**
     * 正则集合
     */
    private static final Pattern[] splitPs = new Pattern[]{
            Pattern.compile("与|、")
    };
    private static final Pattern khPattern = Pattern.compile("\\(.{0,}\\)");
    private static final Pattern numberPattern = Pattern.compile("^[0-9]+$");
    private static final Pattern[] matchPs = new Pattern[]{
            //去出XXX号 XXX-XXX等数据
            Pattern.compile("[a-zA-Z]{0,}[0-9]+(-){0,}[0-9]{0,}.+")
            , Pattern.compile("(等|附近|东|西|南|北|东南|西南|东北|西北|之|交叉口)(?!路).{0,}$")
    };
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
            for (Pattern splitP : splitPs) {
                String[] split = splitP.split(content);
                if (split.length > 1) {
                    set.addAll(Arrays.asList(split));
                }
            }
            return new ArrayList<>(set);
        }
    };

    private static final StringSplitTranser.Func splitFunc2 = new StringSplitTranser.Func() {
        @Override
        public List<String> split(String content) {
            Set<String> set = new HashSet<>();
            set.add(content);
            //拆分
            for (Pattern splitP : splitPs) {
                String[] split = splitP.split(content);
                if (split.length > 1) {
                    set.addAll(Arrays.asList(split));
                }
            }
            //根据正则过滤掉词之后，生成新词
            for (Pattern p : matchPs) {
                Matcher matcher = p.matcher(content);
                String s = matcher.replaceAll("").trim();
                if (StringUtils.isNotBlank(s)) {
                    set.add(s);
                }
            }
            return new ArrayList<>(set);
        }
    };
    private static final ConditionDeleteTranser.Condition condition1 = new ConditionDeleteTranser.Condition() {
        @Override
        public boolean isFilter(DomainElement domainElement) {
            String name = domainElement.get("NAME").toString();
            if (name.contains(";")) {
                return true;
            }
            return false;
        }
    };
    private static final ConditionDeleteTranser.Condition condition2 = new ConditionDeleteTranser.Condition() {
        @Override
        public boolean isFilter(DomainElement domainElement) {
            String name = domainElement.get("word").toString();
            Matcher matcher = numberPattern.matcher(name);
            String trim = matcher.replaceAll("").trim();
            if (name.length() <= 2 || StringUtils.isBlank(trim)) {
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

        IDataSource.Exp exp = new IDataSource.Exp("select DISTINCT ADDRESS AS NAME from PY_AMAP_LBS_INFO where ADDRESS !='[]'");
        new FJSDLWZDataSourceMain().deal(exp, properties.getProperty("es_dict_name"));
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new ConditionDeleteTranser(condition1)
                , new StringSplitTranser("NAME", splitFunc0)
                , new StringSplitTranser("NAME", splitFunc1)
                , new StringSplitTranser("NAME", splitFunc2)
                , new XMDlwzTransIterator()
                , new ConditionDeleteTranser(condition2)
                , new StringDuplicateRemovalTranser("word")
        );
    }

    @Override
    public IDataTarget getDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
//        return new FileTarget("C:/Users/joshua/Desktop/文本提取/福建省地理位置.txt", "word");
    }

    @Override
    public IDataSource getDataSource(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new OracleSource(connection, software);
    }
}

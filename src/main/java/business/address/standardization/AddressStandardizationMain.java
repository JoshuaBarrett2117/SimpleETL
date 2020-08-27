package business.address.standardization;

import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranslator;
import common.source.excel.ExcelFileSourceBuilder;
import common.target.TextFileTarget;
import common.translator.PatternDeleteTranslator;
import dao.core.model.DomainElement;
import utils.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class AddressStandardizationMain extends AbstractMain {
    private static final Pattern p1 = Pattern.compile("([A-Za-z0-9]+)(-*)([0-9]*)(号|米|门|楼|座|栋|区|层|牌).*");
    /**
     * A-2，201-5之类的
     */
    private static final Pattern p2 = Pattern.compile("[a-zA-Z0-9]+-.+");
    /**
     * B13之类的
     */
    private static final Pattern p3 = Pattern.compile("[A-Za-z][0-9]+.+");
    /**
     * 括号
     */
    private static final Pattern p4 = Pattern.compile("(\\().+");
    /**
     * 数字或字母结尾的
     */
    private static final Pattern p5 = Pattern.compile("[a-zA-Z0-9]+$");
    /**
     * 北二层，南100米等内容
     */
    private static final Pattern p6 = Pattern.compile("(东|西|南|北|东南|西南|东北|西北)(行|[0-9]|([一二三四五六七八九]层)).+");

    public static void main(String[] args) {
        new AddressStandardizationMain().deal(null, null);
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return ExcelFileSourceBuilder
                .anExcelFileSource("C:\\Users\\joshua\\Desktop\\地址映射\\高德天津地理数据.xlsx")
                .isFirstRowsAreColumns(true)
                .sheetIndices(Arrays.asList(1))
                .columnNames(Arrays.asList("ID", "PNAME", "ADNAME", "ADDRESS"))
                .build()
                ;
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new PatternDeleteTranslator("ADDRESS", p1),
                new PatternDeleteTranslator("text", p2),
                new PatternDeleteTranslator("text", p3),
                new PatternDeleteTranslator("text", p4),
                new PatternDeleteTranslator("text", p5),
                new PatternDeleteTranslator("text", p6),
                new EndWithDeleteTranslator("text", Arrays.asList("交叉口", "附近", "出口", "对面","交口","后")),
                new IIteratorTranslator() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            @Override
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            @Override
                            public DomainElement next() {
                                DomainElement next = iterator.next();
                                Object address = next.get("ADDRESS");
                                if (address == null
                                        || StringUtils.isBlank(address.toString())
                                        || address.toString().equals("[]")
                                ) {
                                    return null;
                                }
                                Object text = next.get("text");
                                if (text == null || StringUtils.isBlank(text.toString())) {
                                    return null;
                                }
                                address = address.toString().replaceAll(",", "，");
                                next.addProperties("text", "" +
                                        next.get("ID") + "," +
                                        address + "," +
                                        next.get("PNAME") + next.get("ADNAME") + text
                                );
                                return next;
                            }
                        };
                    }
                }

        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
//        return new ElasticsearchTarget(properties);
//        return new ConsoleTarget();
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\高德天津地理数据标准化.csv", "text");
    }
}

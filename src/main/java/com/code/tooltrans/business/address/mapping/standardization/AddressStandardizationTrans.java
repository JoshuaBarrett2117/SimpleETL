package com.code.tooltrans.business.address.mapping.standardization;

import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IIteratorTranslator;
import com.code.tooltrans.common.source.excel.ExcelFileSourceBuilder;
import com.code.tooltrans.common.translator.PatternDeleteTranslator;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public abstract class AddressStandardizationTrans extends AbstractMain {
    private static final Pattern p1 = Pattern.compile("([A-Za-z0-9]+)(-*)([0-9]*)(号|米|门|楼|座|栋|区|层|排).*");
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
     * 数字或字母结尾的,或-结尾的
     */
    private static final Pattern p5 = Pattern.compile("([a-zA-Z0-9]|-|/)+$");
    /**
     * 北二层，南100米等内容
     */
    private static final Pattern p6 = Pattern.compile("(东|西|南|北|东南|西南|东北|西北)(行|[0-9]|([一二三四五六七八九]层)).+");


    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return ExcelFileSourceBuilder
                .anExcelFileSource("C:\\Users\\joshua\\Desktop\\地址映射\\高德天津地理数据.xlsx")
                .isFirstRowsAreColumns(true)
                .sheetIndices(Arrays.asList(1))
                .columnNames(Arrays.asList("ID", "TYPE", "PNAME", "ADNAME", "ADDRESS"))
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
                new EndStrDeleteDeleteTranslator("text", Arrays.asList("交叉口", "附近", "出口", "对面", "交口", "后")),
                //删除方位词
                new EndStrDeleteDeleteTranslator("text", Arrays.asList("东", "西", "南", "北", "东南", "西南", "东北", "西北")),
                new AddressStandardizationTranslator(),
                lastStandardizationTrans()
        );
    }

    /**
     * 最后一个标准化流程
     *
     * @return
     */
    protected abstract IIteratorTranslator lastStandardizationTrans();


}

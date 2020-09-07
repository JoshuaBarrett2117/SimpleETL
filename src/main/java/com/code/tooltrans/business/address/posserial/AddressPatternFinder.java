package com.code.tooltrans.business.address.posserial;

import com.code.common.dao.core.model.DomainElement;
import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.*;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import com.code.tooltrans.common.translator.HanlpSegmentTranslator;
import com.code.tooltrans.common.translator.PatternDeleteTranslator;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

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
public class AddressPatternFinder extends AbstractMain {

    Pattern p1 = Pattern.compile(".*?(道|路|交叉口|交口)");
    Pattern p2 = Pattern.compile(".*?(交叉口|交口)");

    public static void main(String[] args) {
        AddressPatternFinder addressPatternFinder = new AddressPatternFinder();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("src_rdb_user", "cicada");
        properties.setProperty("src_rdb_password", "123456");

        properties.setProperty("target_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("target_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("target_rdb_user", "cicada");
        properties.setProperty("target_rdb_password", "123456");
        AddressPatternFinder.properties = properties;
        IDataSource.Exp exp = new IDataSource.Exp("select address  from tj_gd_dlwz where type not like '%公交车%'");
        addressPatternFinder.deal(exp, "address_pattern_find");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        Properties properties1 = new Properties();
        properties1.setProperty("driverName", properties.getProperty("src_rdb_driverName"));
        properties1.setProperty("url", properties.getProperty("src_rdb_url"));
        properties1.setProperty("user", properties.getProperty("src_rdb_user"));
        properties1.setProperty("password", properties.getProperty("src_rdb_password"));
        RdbDataSource rdbDataSource = new RdbDataSource(properties1);
        RdbSource rdbSource = new RdbSource(rdbDataSource.getConnection(), new Software("oracle"));
        return rdbSource;
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new PatternDeleteTranslator("ADDRESS", p1, true),
                new PatternDeleteTranslator("text", p2, true),
                new HanlpSegmentTranslator("text", HanLP.newSegment()),
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
                                List<Term> segs = (List<Term>) next.getProperties().remove("seg");
                                DomainElement trueNext = new DomainElement();
                                trueNext.addProperties("src_address", next.get("ADDRESS"));
                                trueNext.addProperties("target_address", next.get("text"));
                                StringBuilder posSerialBuilder = new StringBuilder();
                                for (int i = 0; i < 6 ; i++) {
                                    if (i > segs.size() - 1) {
                                        trueNext.addProperties("pos_" + (i + 1), null);
                                    } else {
                                        if (posSerialBuilder.length() != 0) {
                                            posSerialBuilder.append(",");
                                        }
                                        posSerialBuilder.append(segs.get(i).nature.toString());
                                        trueNext.addProperties("pos_" + (i + 1), segs.get(i).nature.toString());
                                    }
                                }
                                trueNext.addProperties("all_pos", posSerialBuilder.toString());
                                return trueNext;
                            }
                        };
                    }
                }
        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        Properties properties1 = new Properties();
        properties1.setProperty("driverName", properties.getProperty("target_rdb_driverName"));
        properties1.setProperty("url", properties.getProperty("target_rdb_url"));
        properties1.setProperty("user", properties.getProperty("target_rdb_user"));
        properties1.setProperty("password", properties.getProperty("target_rdb_password"));
        RdbDataSource rdbDataSource = new RdbDataSource(properties1);
        return new RdbTarget(rdbDataSource.getConnection(), new Software("oracle"));
//        return new ConsoleTarget();
    }
}

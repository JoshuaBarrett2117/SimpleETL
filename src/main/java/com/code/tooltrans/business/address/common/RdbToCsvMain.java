package com.code.tooltrans.business.address.common;

import com.code.common.dao.core.model.DomainElement;
import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.*;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.text.TextFileTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class RdbToCsvMain extends AbstractMain {
    public static void main(String[] args) {
        RdbToCsvMain excelToEsMain = new RdbToCsvMain();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("src_rdb_user", "cicada");
        properties.setProperty("src_rdb_password", "123456");
        RdbToCsvMain.properties = properties;
        IDataSource.Exp exp = new IDataSource.Exp("select * from address_pattern_find");
        excelToEsMain.deal(exp, null);
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
        return Arrays.asList(new IIteratorTranslator() {
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
                        String srcAddress = (String) next.get("SRC_ADDRESS");
                        String targetAddress = (String) next.get("TARGET_ADDRESS");
                        String allPos = (String) next.get("ALL_POS");
                        String pos1 = (String) next.get("POS_1");
                        String pos2 = (String) next.get("POS_2");
                        String pos3 = (String) next.get("POS_3");
                        String pos4 = (String) next.get("POS_4");
                        String pos5 = (String) next.get("POS_5");
                        String pos6 = (String) next.get("POS_6");
                        next.addProperties("text", "" +
                                (srcAddress == null ? "" : srcAddress.replaceAll(",", "，")) + "," +
                                (targetAddress == null ? "" : targetAddress.replaceAll(",", "，")) + "," +
                                (allPos == null ? "" : allPos.replaceAll(",", "，")) + "," +
                                (pos1 == null ? "" : pos1) + "," +
                                (pos2 == null ? "" : pos2) + "," +
                                (pos3 == null ? "" : pos3) + "," +
                                (pos4 == null ? "" : pos4) + "," +
                                (pos5 == null ? "" : pos5) + "," +
                                (pos6 == null ? "" : pos6)
                        );
                        return next;
                    }
                };
            }
        });
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\地址词性规律.csv", "text");
//        return new ConsoleTarget();
    }
}

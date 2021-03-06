package com.code.tooltrans.business.address.common;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.*;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.text.CsvTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class RdbToCsvMain2 extends AbstractMain {
    public static void main(String[] args) {
        RdbToCsvMain2 excelToEsMain = new RdbToCsvMain2();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("src_rdb_user", "cicada");
        properties.setProperty("src_rdb_password", "123456");
        RdbToCsvMain2.properties = properties;
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
            public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
                return new Iterator<DataRowModel>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public DataRowModel next() {
                        return iterator.next();
                    }
                };
            }
        });
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new CsvTarget("C:\\Users\\joshua\\Desktop\\地址映射\\地址词性规律2.csv",
                Arrays.asList("SRC_ADDRESS", "TARGET_ADDRESS", "ALL_POS", "POS_1", "POS_2", "POS_3", "POS_4", "POS_5", "POS_6"));
//        return new ConsoleTarget();
    }
}

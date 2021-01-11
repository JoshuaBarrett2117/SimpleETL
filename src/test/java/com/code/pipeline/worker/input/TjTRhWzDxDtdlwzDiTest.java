package com.code.pipeline.worker.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.datacleaning.IAddressDataCleaning;
import com.code.datacleaning.impl.AddressDataCleaningImpl;
import com.code.tooltrans.common.*;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.ConsoleTarget;
import com.code.tooltrans.common.target.elasticsearch.ElasticsearchTarget;
import com.code.tooltrans.common.translator.ElementSplitTranslator;

import java.util.*;

/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved.
 * ====================================================
 * 文件名称: Assert.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/8/28     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class TjTRhWzDxDtdlwzDiTest extends AbstractMain {
    public static void main(String[] args) {
        TjTRhWzDxDtdlwzDiTest gd = new TjTRhWzDxDtdlwzDiTest();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
        properties.setProperty("src_rdb_user", "iof");
        properties.setProperty("src_rdb_password", "dragon");

        properties.setProperty("target_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("target_rdb_url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
        properties.setProperty("target_rdb_user", "iof");
        properties.setProperty("target_rdb_password", "dragon");

        properties.setProperty("esIp", "192.168.125.5");
        properties.setProperty("esPort", "9400");
        TjTRhWzDxDtdlwzDiTest.properties = properties;
        //行政区划数据
        IDataSource.Exp exp3 = new IDataSource.Exp("" +
                "SELECT CITYNAME AS SHIMC,ADNAME AS QXMC,ADDRESS AS XXDZ,TYPE,location FROM TJ_GD_DLWZ " +
                "WHERE TYPE NOT LIKE '%交通设施服务%'" +
                "and TYPE NOT LIKE '%充电站%'" +
                "and ADDRESS != '[]'");
        gd.deal(exp3, "t_rh_wz_dx_dtdlwz_di_210108");
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
        List<IIteratorTranslator> translators = new ArrayList<>();
        List<IIteratorTranslator> result = new ArrayList<>();
        result.add(new ElementSplitTranslator(new CleanFuncTranslator()));
        result.add(new LocationTranslator());
        result.addAll(translators);
        return result;
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }

    private class CleanFuncTranslator implements ElementSplitTranslator.Func {
        IAddressDataCleaning dataCleaning = new AddressDataCleaningImpl();

        @Override
        public List<DataRowModel> split(DataRowModel content) {
            List<Map<String, String>> address = dataCleaning.cleanToMapList(content.getAsString("XXDZ"), null, content.getAsString("SHIMC"), content.getAsString("QXMC"), null);
            List<DataRowModel> result = new ArrayList<>();
            for (Map<String, String> stringDataRowModel : address) {
                DataRowModel temp = new DataRowModel();
                temp.setProperties(stringDataRowModel);
                result.add(temp);
            }
            return result;
        }
    }

    private class LocationTranslator implements IIteratorTranslator {
        @Override
        public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
            return new Iterator<DataRowModel>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public DataRowModel next() {
                    DataRowModel next = iterator.next();
                    String location = next.getAsString("LOCATION");
                    next.setId(next.getAsString("ZJ_ID"));
                    if (location != null) {
                        String[] split = location.split(",");
                        if (split.length == 2) {
                            String jd = split[0];
                            String wd = split[1];
                            next.addProperties("JD", Double.valueOf(jd));
                            next.addProperties("WD", Double.valueOf(wd));
                        }
                    }
                    next.removeProperties("LOCATION");
                    return next;
                }
            };
        }
    }
}

package com.code.tooltrans.business.address.dimension;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.*;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import com.code.tooltrans.common.translator.StringDuplicateRemovalTranslator;

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
public class DimensionTableImportMain extends AbstractMain {
    public static void main(String[] args) {
        DimensionTableImportMain dimensionTableImportMain = new DimensionTableImportMain();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("src_rdb_user", "cicada");
        properties.setProperty("src_rdb_password", "123456");

        properties.setProperty("target_rdb_driverName", "com.pivotal.jdbc.GreenplumDriver");
        properties.setProperty("target_rdb_url", "jdbc:pivotal:greenplum://192.168.125.8:5432");
        properties.setProperty("target_rdb_user", "gpadmin");
        properties.setProperty("target_rdb_password", "gpadmin");

        DimensionTableImportMain.properties = properties;
        //行政区划数据
        IDataSource.Exp exp = new IDataSource.Exp("" +
                "SELECT " +
                "ZJ_ID,GJDQMC,SMC,SHIMC,QXMC,XZJDMC,JLXMC,XXDZ,JD," +
                "WD,BM,DDMC,DDDM,FJDDMC,FJDD_ID,BZDM FROM TJ_T_RH_WZ_DX_DTDLWZ_DI " +
                "WHERE TYPE!='居委会信息'");
        dimensionTableImportMain.deal(exp, "T_RH_WZ_DX_DTDLWZ_DI");
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
                new IIteratorTranslator() {
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
                                next.addProperties("ZJ_ID", UUID.randomUUID().toString().replace("-", ""));
                                Object qxmc = next.get("QXMC");
                                if (qxmc != null) {
                                    String s = qxmc.toString();
                                    if (!s.equals("天津市")) {
                                        next.addProperties("QXMC", s.replace("天津市", ""));
                                    }
                                }
                                Object ddmc = next.get("DDMC");
                                if (ddmc != null) {
                                    String s = ddmc.toString();
                                    if (!s.equals("天津市")) {
                                        next.addProperties("DDMC", s.replace("天津市", ""));
                                    }
                                }
                                return next;
                            }
                        };
                    }
                },
                new StringDuplicateRemovalTranslator("BZDM")
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
        RdbTarget rdbTarget = new RdbTarget(rdbDataSource.getConnection(), new Software("greenplum"));
        return rdbTarget;
    }
}

package com.code.pipeline.etl.input;

import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;

import java.util.Properties;

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
public class RdbToRdbMain extends AbstractMain {
    public static void main(String[] args) {
        RdbToRdbMain rdbToRdbMain = new RdbToRdbMain();
        Properties properties = new Properties();
        properties.setProperty("src_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("src_rdb_url", "jdbc:oracle:thin:@192.168.6.18:1521:orcl");
        properties.setProperty("src_rdb_user", "cicada");
        properties.setProperty("src_rdb_password", "123456");

        properties.setProperty("target_rdb_driverName", "oracle.jdbc.OracleDriver");
        properties.setProperty("target_rdb_url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
        properties.setProperty("target_rdb_user", "iof");
        properties.setProperty("target_rdb_password", "dragon");

        RdbToRdbMain.properties = properties;
        //行政区划数据
        IDataSource.Exp exp = new IDataSource.Exp("" +
                "SELECT * FROM TJ_GD_STATISTICS");
        rdbToRdbMain.deal(exp, "TJ_GD_STATISTICS_XX2");
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
    protected IDataTarget buildDataTarget(Properties properties) {
        Properties properties1 = new Properties();
        properties1.setProperty("driverName", properties.getProperty("target_rdb_driverName"));
        properties1.setProperty("url", properties.getProperty("target_rdb_url"));
        properties1.setProperty("user", properties.getProperty("target_rdb_user"));
        properties1.setProperty("password", properties.getProperty("target_rdb_password"));
        RdbDataSource rdbDataSource = new RdbDataSource(properties1);
        RdbTarget rdbTarget = new RdbTarget(rdbDataSource.getConnection(), new Software("oracle"));
        return rdbTarget;
    }
}

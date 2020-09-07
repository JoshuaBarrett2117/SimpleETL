package com.code.tooltrans.business.address.dimension;

import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.elasticsearch.ElasticsearchTarget;

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
public class DimensionTableToEsMain extends AbstractMain {
    public static void main(String[] args) {
        DimensionTableToEsMain dimensionTableToEsMain = new DimensionTableToEsMain();
        Properties properties = new Properties();
        properties.setProperty("driverName", "com.pivotal.jdbc.GreenplumDriver");
        properties.setProperty("url", "jdbc:pivotal:greenplum://192.168.125.8:5432");
        properties.setProperty("user", "gpadmin");
        properties.setProperty("password", "gpadmin");

        properties.setProperty("esIp", "192.168.125.5");
        properties.setProperty("esPort", "9400");

        DimensionTableToEsMain.properties = properties;
        //行政区划数据
        IDataSource.Exp exp = new IDataSource.Exp("select zj_id,bzdm,shimc,qxmc from T_RH_WZ_DX_DTDLWZ_DI where qxmc !='天津市' and qxmc !='天津'");
        dimensionTableToEsMain.deal(exp, "t_rh_wz_dx_dtdlwz_di_hanlp");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        RdbSource rdbSource = new RdbSource(rdbDataSource.getConnection(), new Software("greenplum"));
        return rdbSource;
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        ElasticsearchTarget elasticsearchTarget = new ElasticsearchTarget(properties);
        return elasticsearchTarget;
    }
}

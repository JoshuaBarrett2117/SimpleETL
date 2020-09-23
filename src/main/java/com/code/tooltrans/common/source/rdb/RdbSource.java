package com.code.tooltrans.common.source.rdb;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.JdbcOperator;
import com.code.common.dao.jdbc.operator.Software;
import com.code.tooltrans.common.IDataSource;

import java.sql.Connection;
import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:09
 */
public class RdbSource implements IDataSource {
    private Connection connection;
    private Software software;

    public RdbSource(Connection connection, Software software) {
        this.connection = connection;
        this.software = software;
    }

    @Override
    public DataRowModel queryForObject(Exp sql) {
        return null;
    }

    @Override
    public Iterator<DataRowModel> iterator(Exp sql) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        return operator.queryForIterator(sql.getExp(), null);
    }
}

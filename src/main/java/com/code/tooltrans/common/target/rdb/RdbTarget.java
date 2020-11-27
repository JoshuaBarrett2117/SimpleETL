package com.code.tooltrans.common.target.rdb;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.JdbcOperator;
import com.code.common.dao.jdbc.operator.Software;
import com.code.common.dao.jdbc.util.DbUtil;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.target.AbstractTarget;

import java.sql.Connection;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:09
 */
public class RdbTarget extends AbstractTarget {
    private Connection connection;
    private Software software;

    public RdbTarget(Connection connection, Software software) {
        this.connection = connection;
        this.software = software;
    }

    @Override
    public boolean save(List<DataRowModel> docs, String indexName) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        for (DataRowModel doc : docs) {
            operator.save(indexName, doc);
        }
        operator.commit();
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        for (DataRowModel doc : docs) {
            operator.saveOrUpdate(indexName, doc);
        }
        operator.commit();
        return true;
    }

    @Override
    public boolean update(List<DataRowModel> docs, String indexName, String idField) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        for (DataRowModel doc : docs) {
            operator.update(indexName, doc, idField);
        }
        operator.commit();
        return true;
    }

    @Override
    public boolean close() {
        DbUtil.close(null, null, connection);
        return true;
    }
}

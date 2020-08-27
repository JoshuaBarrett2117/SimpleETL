package common.target;

import dao.jdbc.operator.JdbcOperator;
import  dao.core.model.DomainElement;
import dao.jdbc.operator.Software;
import common.IDataTarget;

import java.sql.Connection;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:09
 */
public class OracleTarget implements IDataTarget {
    private Connection connection;
    private Software software;

    public OracleTarget(Connection connection, Software software) {
        this.connection = connection;
        this.software = software;
    }

    @Override
    public boolean save(List<DomainElement> docs, String indexName) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        for (DomainElement doc : docs) {
            operator.save(indexName, doc);
        }
        operator.commit();
        return true;
    }

    @Override
    public boolean saveOrUpdate(List<DomainElement> docs, String indexName) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        for (DomainElement doc : docs) {
            operator.saveOrUpdate(indexName, doc);
        }
        operator.commit();
        return true;
    }
}

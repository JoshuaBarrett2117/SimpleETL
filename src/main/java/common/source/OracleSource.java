package common.source;

import com.code.common.dao.jdbc.operator.JdbcOperator;
import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import common.IDataSource;

import java.sql.Connection;
import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:09
 */
public class OracleSource implements IDataSource {
    private Connection connection;
    private Software software;

    public OracleSource(Connection connection, Software software) {
        this.connection = connection;
        this.software = software;
    }

    @Override
    public Iterator<DomainElement> iterator(Exp sql) {
        JdbcOperator operator = new JdbcOperator(connection, software);
        return operator.queryForIterator(sql.getExp(), null);
    }
}

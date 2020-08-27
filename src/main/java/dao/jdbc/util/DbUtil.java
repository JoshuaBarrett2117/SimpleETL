
package dao.jdbc.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbUtil {


    public static final void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        close(rs);
        close(ps);
        close(conn);
    }

    public static final void close(AutoCloseable... autoCloseable) {
        for (AutoCloseable closeable : autoCloseable) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }


    public static final void close(ResultSet rs, Statement ps, Connection conn) {
        close(rs);
        close(ps);
        close(conn);
    }
}

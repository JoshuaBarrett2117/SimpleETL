package common;

import java.sql.*;
import java.util.Properties;

/**
 * - 数据的连接以及关闭
 * <p>
 * -
 * <p>
 * - @author Administrator
 */
public class RdbDataSource {
    private String driverName;
    private String url;
    private String user;
    private String password;

    public RdbDataSource(Properties properties) {
        this(properties.getProperty("driverName"),
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password"));
    }

    public RdbDataSource(String driverName, String url, String user, String password) {
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            // 加载驱动
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * - 获取数据库的连接对象(Connection对象)
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * - 关闭数据库
     */
    public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
        

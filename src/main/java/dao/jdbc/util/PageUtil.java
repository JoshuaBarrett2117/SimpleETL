package dao.jdbc.util;


import dao.core.model.PageInfo;
import dao.jdbc.operator.Software;

/**
 * @author liufei
 * @date 2019/5/15 13:39
 */
public class PageUtil {

    public static String page(String sql, PageInfo pageInfo, Software software) {
        if (pageInfo == null) {
            return sql;
        }
        String pageSql = "";
        int pageSize = pageInfo.getPageSize();
        int pageIndex = pageInfo.getPageIndex();
        String dbType = software.getCode();
        if (dbType.equalsIgnoreCase("ORACLE")) {
            pageSql += "SELECT * FROM " +
                    "( " +
                    "SELECT A.*, ROWNUM RN " +
                    "FROM (" + sql + ") A " +
                    "WHERE ROWNUM <= " + pageIndex * pageSize +
                    ") " +
                    "WHERE RN >  " + (pageIndex - 1) * pageSize;
        } else if (dbType.equalsIgnoreCase("MYSQL") || dbType.equalsIgnoreCase("postgreSQL")) {
            pageSql += (sql + "  limit " + pageSize + " offset " + (pageIndex - 1) * pageSize);
        } else {
            throw new UnsupportedOperationException("暂时不支持：[" + dbType + "]的分页操作！！");
        }
        return pageSql;
    }
}

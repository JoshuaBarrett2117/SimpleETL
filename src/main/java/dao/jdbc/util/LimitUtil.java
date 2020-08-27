package dao.jdbc.util;


import dao.jdbc.operator.Software;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liufei
 * @date 2019/5/15 10:45
 */
public class LimitUtil {

    public static String limit(String originalSql, int limit, Software software) {
        if (limit < 0) {
            return originalSql;
        }
        String limitSql = "";
        String dbType = software.getCode();
        if (dbType.equalsIgnoreCase("ORACLE")) {
            limitSql = oracleLimit(originalSql, limit, limitSql);
        } else if (dbType.equalsIgnoreCase("MYSQL") || dbType.equalsIgnoreCase("postgreSQL")) {
            limitSql = mysqlLimit(limit, limitSql);
        } else {
            throw new UnsupportedOperationException("暂时不支持：[" + dbType + "]的限制配置！！");
        }
        return originalSql + limitSql;
    }

    private static String mysqlLimit(int limit, String limitSql) {
        limitSql += "limit " + limit;
        return limitSql;
    }

    private static String oracleLimit(String originalSql, int limit, String limitSql) {
        if (hasWhere(originalSql)) {
            limitSql += " AND ( ROWNUM <= " + limit + ")";
        } else {
            limitSql += " WHERE ( ROWNUM <= " + limit + ")";
        }
        return limitSql;
    }

    private static boolean hasWhere(String originalSql) {
        //查询最外层
        return excludeString(originalSql).toUpperCase().contains("WHERE");
    }

    /**
     * 删除括号中内容
     *
     * @param str
     * @return
     */
    public static String excludeString(String str) {
        List<String> list = new ArrayList<String>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (str.charAt(i) == ')') {
                endFlag++;
                if (endFlag == startFlag) {
                    list.add(str.substring(start + 1, i));
                }
            }
        }
        for (String s : list) {
            str = str.replace(s, "");
        }
        return str;
    }
}

package dao.jdbc.util;


import dao.jdbc.operator.Software;
import dao.core.param.IncrementParam;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IncrementUtil {

    public static void main(String[] args) {
        String tableName = "pipeline";
        IncrementParam ip = new IncrementParam("ff", new Date(1547049600000L));
        Software software = new Software();
        software.setCode("postgresql");
        String s = new IncrementUtil().constructIncrementSql("select * from " + tableName + " where 1=1", ip, software);
        System.out.println(s);
    }

    public String constructIncrementSql(String originalSql, IncrementParam ip, Software software) {
        String incrSql = "";

        if (null != ip) {
            //sql += " WHERE 1=1 ";
            //如果最后更新时间戳不为空，则使用最后更新时间戳作为查询条件
            // WHERE minVal <= updateTime <= maxVal
            // WHERE lastUpdate <= updateTime <= maxVal
            if (originalSql.toUpperCase().contains(" WHERE ")) {
                incrSql += " AND (";
            } else {
                incrSql += " WHERE (";
            }
            IncrementSqlBuilder incrementSqlBuilder = null;

            if (software.getCode().equalsIgnoreCase("Greenplum")) {
                incrementSqlBuilder = new GreenplumIncrementSql();
            } else if ("Dm".equalsIgnoreCase(software.getCode())) {
                incrementSqlBuilder = new DmIncrementSql();
            } else {
                incrementSqlBuilder = new DefaultIncrementSql();
            }

            if (software.getCode().equalsIgnoreCase("ORACLE")) {
                incrementSqlBuilder = new OracleIncrementSql();
            } else if (software.getCode().equalsIgnoreCase("PostgreSql")) {
                incrementSqlBuilder = new PostgreSqlIncrementSql();
            }

            incrSql = incrSql + incrementSqlBuilder.getIncrementSql(ip);

            incrSql = incrSql + ")";
        }

        return originalSql + incrSql;
    }

    public abstract class IncrementSqlBuilder {
        public String getIncrementSql(IncrementParam ip) {
            switch (ip.getIncrementType()) {
                case TS:
                    return compareSql(ip.getColumnName(), ip.getLastUpdate());
                case STR:
                    return compareSql(ip.getColumnName(), ip.getLastUpdateStr());
                case SEQUENCE_LONG:
                    return ip.getColumnName() + " >  '" + ip.getLastSequenceLong() + "' ";
                case SEQUENCE_INT:
                    return ip.getColumnName() + " >  '" + ip.getLastSequenceInt() + "' ";
                default:
                    throw new RuntimeException("不支持的增量类型");
            }
        }

        public abstract String compareSql(String columnName, String lastUpdate);

        public abstract String compareSql(String columnName, Date lastUpdate);

    }

    class GreenplumIncrementSql extends IncrementSqlBuilder {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        @Override
        public String compareSql(String columnName, String lastUpdate) {
            String sql = "\"" + columnName + "\" > to_timestamp('" + lastUpdate + "', 'yyyyMMddHH24MISSMS')";
            return sql;
        }

        @Override
        public String compareSql(String columnName, Date lastUpdate) {
            return compareSql(columnName, sdf.format(lastUpdate));
        }

    }

    public class OracleIncrementSql extends IncrementSqlBuilder {

        @Override
        public String compareSql(String columnName, String lastUpdate) {
            String sql = "(" + columnName
                    + " > to_date(TO_CHAR("
                    + lastUpdate
                    + " / (1000 * 60 * 60 * 24) + TO_DATE('19700101080000', 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS'), 'yyyymmddhh24miss')";
            sql = sql + ")";
            return sql;
        }

        @Override
        public String compareSql(String columnName, Date lastUpdate) {
            return compareSql(
                    columnName,
                    String.valueOf(lastUpdate.getTime())
            );
        }
    }

    class PostgreSqlIncrementSql extends IncrementSqlBuilder {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        @Override
        public String compareSql(String columnName, String lastUpdate) {
            String sql = "\"" + columnName + "\" > to_timestamp('" + lastUpdate + "', 'yyyyMMddHH24MISSMS')";
            return sql;
        }

        @Override
        public String compareSql(String columnName, Date lastUpdate) {
            return compareSql(columnName, sdf.format(lastUpdate));
        }
    }

    class DmIncrementSql extends IncrementSqlBuilder {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String compareSql(String columnName, String lastUpdate) {
            String format = "YYYY-MM-DD HH:MI:SS";
            String sql = String.format("TO_DATE(\"%s\", '%s') > TO_DATE('%s', '%s')", columnName, format, lastUpdate, format);
            return sql;
        }

        @Override
        public String compareSql(String columnName, Date lastUpdate) {
            return compareSql(columnName, sdf.format(lastUpdate));
        }
    }

    class DefaultIncrementSql extends IncrementSqlBuilder {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        @Override
        public String compareSql(String columnName, String lastUpdate) {
            return "(DATE_FORMAT(" + columnName + ", '%Y%m%d%H%i%s') > STR_TO_DATE('" + lastUpdate + "', '%Y%m%d%H%i%s')) = 1";
        }

        @Override
        public String compareSql(String columnName, Date lastUpdate) {
            return compareSql(columnName, sdf.format(lastUpdate));
        }
    }
}

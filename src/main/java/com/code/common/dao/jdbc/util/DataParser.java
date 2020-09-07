package com.code.common.dao.jdbc.util;


import com.code.common.dao.jdbc.operator.Software;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将数据解析成字符sql可识别的字符串的工具类
 *
 * @author liufei
 * @date 2019/5/15 20:43
 */
public class DataParser {

    public static void main(String[] args) {
        Software software = new Software("dm");
        System.out.println("select " + parse(new Date(), software) + " as time");
    }

    public static String parse(Object value, Software software) {
        if (value instanceof String) {
            return "'" + value + "'";
        } else if (value instanceof Number) {
            return String.valueOf(value);
        } else if (value instanceof Date) {
            return parseDate((Date) value, software);
        } else {
            return value.toString();
        }
    }

    private static String parseDate(Date date, Software software) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dbType = software.getCode();
        if (dbType.equalsIgnoreCase("ORACLE") || dbType.equalsIgnoreCase("Greenplum") || dbType.equalsIgnoreCase("postgreSQL")) {
            return "to_timestamp('" + sdf.format(date) + "','yyyymmddhh24miss')";
        } else if (dbType.equalsIgnoreCase("dm")) {
            return "to_date('" + sdf.format(date) + "','yyyymmddhh24miss')";
        } else if (dbType.equalsIgnoreCase("MYSQL")) {
            return "TIMESTAMP('" + sdf.format(date) + "')";
        } else {
            return "STR_TO_DATE('" + sdf.format(date) + "', '%Y%m%d%H%i%s')";
        }
    }
}

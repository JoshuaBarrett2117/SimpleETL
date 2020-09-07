package com.code.common.dao.jdbc.util;


import com.code.common.dao.core.model.DomainElement;
import com.code.common.dao.jdbc.operator.Software;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

/**
 * SaveOrUpdate语法与关系库类型有关
 *
 * @author liufei
 * @date 2019/5/15 21:04
 */
public class SaveOrUpdateCreater {
    public static void main(String[] args) {
        Software software = new Software("MYSql");
        DomainElement element = new DomainElement();
        element.addProperties("field1", "1");
        element.addProperties("field2", 2);
        element.addProperties("field3", new Timestamp(System.currentTimeMillis()));
        System.out.println(create("table1", element, software));
        software.setCode("postgresql");
        System.out.println(create("table2", element, software));
        software.setCode("ORACLE");
        System.out.println(create("table3", element, software));
    }

    public static String create(String tableName, DomainElement domainElement, Software software) throws IllegalArgumentException {
        Map<String, Object> properties = domainElement.getProperties();
        if (software.getCode().equalsIgnoreCase("ORACLE")) {
            return oracleSql(tableName, properties);
        } else if (software.getCode().equalsIgnoreCase("postgresql")) {
            return postgreSql(tableName, properties);
        } else if (software.getCode().equalsIgnoreCase("mysql")) {
            return mySql(tableName, properties);
        } else {
            throw new IllegalArgumentException("暂不支持【" + software.getCode() + "】的saveOrUpdate功能");
        }
    }

    private static String mySql(String tableName, Map<String, Object> properties) {
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        StringBuffer sql = new StringBuffer();
        sql.append(getInsertInto(tableName, entries));
        sql.append(" on  DUPLICATE KEY UPDATE ");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(entry.getKey() + "=:" + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        return sql.toString();
    }

    private static String postgreSql(String tableName, Map<String, Object> properties) {
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        StringBuffer sql = new StringBuffer();
        sql.append(getInsertInto(tableName, entries));

        sql.append(" ON conflict(ID) DO UPDATE SET ");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(entry.getKey() + "=:" + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        return sql.toString();
    }

    private static String getInsertInto(String tableName, Set<Map.Entry<String, Object>> entries) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + tableName + "(ID,");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ')');
        sql.append("VALUES(:ID,");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(":" + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ')');
        return sql.toString();
    }

    private static String oracleSql(String tableName, Map<String, Object> properties) {
        StringBuffer sql = new StringBuffer();
        sql.append("merge into " + tableName + " T1 USING ( select ");
        sql.append(":ID as id ,");
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(":" + entry.getKey() + " as " + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append("from dual) T2 on (T1.id = T2.id)");
        sql.append("WHEN MATCHED THEN UPDATE SET ");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append("T1." + entry.getKey() + " = T2." + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append("WHEN NOT MATCHED THEN  INSERT (ID,");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append(entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append(")VALUES(T2.ID,");
        for (Map.Entry<String, Object> entry : entries) {
            sql.append("T2." + entry.getKey());
            sql.append(",");
        }
        sql.setCharAt(sql.length() - 1, ')');
        return sql.toString();
    }
}

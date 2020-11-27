package com.code.common.dao.jdbc.operator;


import com.code.common.dao.core.condition.NestedCondition;
import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.core.model.PageInfo;
import com.code.common.dao.core.param.ExpParam;
import com.code.common.dao.core.type.TypeTransformer;
import com.code.common.dao.jdbc.util.*;
import io.searchbox.strings.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * jdbc操作
 *
 * @author liufei
 * @date 2019/5/14 10:18
 */
public class JdbcOperator {
    private static final Logger logger = LoggerFactory.getLogger(JdbcOperator.class);
    private Connection conn;
    private Map<Integer, ColumnMeta> columnMetas;
    private PreparedStatement pst;
    private ResultSet res;
    private Software software;
    private static final int SCROLL_FETCH_SIZE = 1000;
    private static final int LIST_MAX_SIZE = 5000;


    public JdbcOperator() {
    }

    public JdbcOperator(Connection conn, Software software) {
        this.conn = conn;
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.software = software;
    }

    public void delete(String tableName, DataRowModel object) {
        StringBuffer sql = new StringBuffer("delete from " + tableName + " ");
        StringBuffer whereBuffer = new StringBuffer();
        Map<String, Object> properties = object.getProperties();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        ExpParam param = new ExpParam();
        for (Map.Entry<String, Object> entry : entries) {
            if (whereBuffer.length() != 0) {
                whereBuffer.append(" and ");
            }
            whereBuffer.append(entry.getKey() + " = :" + entry.getKey());
            param.addParam(entry.getKey(), entry.getValue());
        }
        if (whereBuffer.length() != 0) {
            sql.append(" where ");
            sql.append(whereBuffer);
        }
        executeExp(sql.toString(), Arrays.asList(param).iterator());
    }

    public void deleteByCondition(String tableName, NestedCondition condition) {
        String sql = createDeleteSql(tableName, condition);
        executeExp(sql, null);
    }

    private String createDeleteSql(String tableName, NestedCondition condition) {
        StringBuffer sql = new StringBuffer();
        if (condition == null) {
            sql.append("delete from " + tableName);
        } else {
            if (StringUtils.isNotBlank(condition.getRawQueryString())) {
                sql.append(condition.getRawQueryString());
            } else {
                sql.append("delete from ");
                sql.append(tableName);
                sql.append(" ");
                sql.append("where ");
                sql.append(PredicateParser.buildSqlCondition(condition, software));
            }
        }
        return sql.toString();
    }


    public void update(String tableName, DataRowModel object, String idField) {
        if (StringUtils.isBlank(object.getId())) {
            throw new IllegalArgumentException("id不允许为空");
        }

        StringBuffer sql = new StringBuffer("update " + tableName + " set ");
        Map<String, Object> properties = object.getProperties();
        Set<String> keys = properties.keySet();
        ExpParam param = new ExpParam();
        for (String key : keys) {
            sql.append(key + "= :" + key + ",");
            Object value = properties.get(key);
            param.addParam(key, value);
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append("where " + idField + " = :" + idField);
        param.addParam(idField,object.getId());
        executeExp(sql.toString(), Arrays.asList(param).iterator());
    }

    private Object getUpdateValue(Object o) {
        if (o instanceof String) {
            return "'" + o + "'";
        }
        return o;
    }

    public void save(String tableName, DataRowModel object) {
        StringBuffer sql = new StringBuffer("insert into " + tableName + "(");
        Map<String, Object> properties = object.getProperties();
        Set<String> keys = properties.keySet();
        ExpParam param = new ExpParam();
        for (String key : keys) {
            sql.append(key + ",");
        }
        sql.setCharAt(sql.length() - 1, ')');
        sql.append("values(");
        for (String key : keys) {
            Object value = properties.get(key);
            param.addParam(key, value);
            sql.append(":" + key + ",");
        }
        sql.setCharAt(sql.length() - 1, ')');
        executeExp(sql.toString(), Arrays.asList(param).iterator());
    }

    private Object getParamValue(Object obj) {
        if (obj instanceof Date) {
            return new Timestamp(((Date) obj).getTime());
        } else {
            return obj;
        }
    }

    public void saveOrUpdate(String tableName, DataRowModel object) {
        String sql = SaveOrUpdateCreater.create(tableName, object, software);
        ExpParam param = new ExpParam();
        param.addParam("ID", object.getId());
        Set<Map.Entry<String, Object>> entries = object.getProperties().entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            param.addParam(entry.getKey(), entry.getValue());
        }
        executeExp(sql, Arrays.asList(param).iterator());
    }

    public void executeExp(String sql, Iterator<ExpParam> params) {
        String parseSql = sql;
        Map<String, List<Integer>> paramMap = null;
        if (params != null) {
            parseSql = parseSql(sql);
            paramMap = parseParam(sql);
        }
        try {
            if (pst == null || pst.isClosed()) {
                logger.debug(parseSql);
                pst = conn.prepareStatement(parseSql);
            }
            if (params != null) {
                while (params.hasNext()) {
                    Map<String, Object> map = params.next().param();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        if (isNumber(key)) {
                            pst.setObject(Integer.valueOf(key), getParamValue(entry.getValue()));
                        } else {
                            List<Integer> integers = paramMap.get(key);
                            for (Integer integer : integers) {
                                pst.setObject(integer, getParamValue(entry.getValue()));
                            }
                        }
                    }
                    pst.addBatch();
                }
            } else {
                pst.addBatch();
            }
        } catch (SQLException e) {
            logger.error("执行sql【" + sql + "】失败！", e);
            end();
            throw new RuntimeException("执行sql【" + sql + "】失败！", e);
        }
    }

    /**
     * 是否是数字
     *
     * @param str
     * @return
     */
    private boolean isNumber(String str) {
        for (char c : str.toCharArray()) {
            if (c > '9' || c < '0') {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析带:param的sql，将其转换成jdbc可以接受的sql
     *
     * @param sql hql参数风格的sql
     * @return
     */
    private String parseSql(String sql) {
        int sqlLength = sql.length();
        int index = 0;
        StringBuffer sb = new StringBuffer();
        while (true) {
            //找到 : 的索引
            int i = sql.indexOf(':', index);
            if (i == -1) {
                break;
            }
            sb.append(sql.substring(index, i));
            sb.append("?");
            index = findParamEnd(sql, i, ' ', ',', ')');
            if (index == -1) {
                index = sqlLength;
                break;
            }
        }
        sb.append(sql.substring(index, sqlLength));
        return sb.toString();
    }

    private int findParamEnd(String sql, int i, char... chars) {
        int index = Integer.MAX_VALUE;
        for (char c : chars) {
            int j = sql.indexOf(c, i);
            if (j != -1 && j < index) {
                index = j;
            }
        }
        if (index == Integer.MAX_VALUE) {
            return -1;
        } else {
            return index;
        }
    }

    private Map<String, List<Integer>> parseParam(String sql) {
        Map<String, List<Integer>> paramMap = new HashMap<>();
        int sqlLength = sql.length();
        int index = 0;
        int paramIndex = 1;
        while (true) {
            //找到 : 的索引
            int i = sql.indexOf(':', index);
            if (i == -1) {
                break;
            }
            index = findParamEnd(sql, i, ' ', ',', ')');
            if (index == -1) {
                index = sqlLength;
                putParamMap(paramMap, sql.substring(i + 1, index).trim(), paramIndex++);
                break;
            }
            putParamMap(paramMap, sql.substring(i + 1, index).trim(), paramIndex++);
        }
        return paramMap;
    }

    private void putParamMap(Map<String, List<Integer>> paramMap, String key, int i) {
        if (paramMap.containsKey(key)) {
            paramMap.get(key).add(i);
        } else {
            List<Integer> temp = new ArrayList<>();
            temp.add(i);
            paramMap.put(key, temp);
        }
    }

    public String preprocessSql(String sql, NestedCondition condition, PageInfo pageInfo) {
        if (condition != null) {
            sql = new IncrementUtil().constructIncrementSql(sql, condition.getIncrementParam(), software);
        }
        sql = LimitUtil.limit(sql, -1, software);
        sql = PageUtil.page(sql, pageInfo, software);
        return sql;
    }

    public Iterator<DataRowModel> queryForIterator(String sql, NestedCondition condition) {
        sql = preprocessSql(sql, condition, null);
        try {
            executeQuery(sql, SCROLL_FETCH_SIZE);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("执行sql出错", e);
            end();
            throw new RuntimeException("执行sql出错", e);
        }
        return new Iterator<DataRowModel>() {
            private boolean isNext = true;

            @Override
            public boolean hasNext() {
                if (null == res) {
                    return false;
                }
                try {
                    if (isNext && res.next()) {
                        isNext = false;
                    }
                    if (isNext) {
                        end();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    end();
                    throw new RuntimeException(e);
                }
                return !isNext;
            }

            @Override
            public DataRowModel next() {
                DataRowModel dataRowModel;
                try {
                    dataRowModel = getResValue(columnMetas, res);
                } catch (SQLException e) {
                    end();
                    throw new RuntimeException("解析数据集异常", e);
                }
                isNext = true;
                return dataRowModel;
            }
        };
    }

    public List<DataRowModel> queryForList(String sql, NestedCondition condition) {
        List<DataRowModel> resultList = new ArrayList<>();
        sql = preprocessSql(sql, condition, null);
        try {
            executeQuery(sql, LIST_MAX_SIZE);
            while (res.next()) {
                DataRowModel dataRowModel = getResValue(columnMetas, res);
                resultList.add(dataRowModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("sql执行出错", e);
            throw new RuntimeException("执行sql出错", e);
        } finally {
            end();
        }
        return resultList;
    }

    public DataRowModel queryForObject(String sql, NestedCondition condition) {
        DataRowModel dataRowModel = null;
        sql = preprocessSql(sql, condition, null);
        try {
            executeQuery(sql, 1);
            if (res.next()) {
                dataRowModel = getResValue(columnMetas, res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("sql执行出错", e);
            throw new RuntimeException("执行sql出错", e);
        } finally {
            end();
        }
        return dataRowModel;
    }

    public PageInfo queryForPage(String sql, NestedCondition condition, PageInfo pageInfo) {
        PageInfo resultPage = new PageInfo();
        List<DataRowModel> dataRowModels = new ArrayList<>();
        resultPage.setDataList(dataRowModels);
        sql = preprocessSql(sql, condition, pageInfo);
        try {
            executeQuery(sql, pageInfo.getPageSize());
            while (res.next()) {
                DataRowModel dataRowModel = getResValue(columnMetas, res);
                dataRowModels.add(dataRowModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("执行sql出错", e);
        } finally {
            end();
        }
        return resultPage;
    }

    public long count(String sql, ExpParam param) {
        if (!sql.toUpperCase().contains("COUNT(")) {
            sql = "SELECT COUNT(1) AS COUNT FROM (" + sql + ")";
        }
        DataRowModel dataRowModel = queryForObject(sql, null);
        long count = ((Number) dataRowModel.get("COUNT")).longValue();
        return count;
    }

    private void executeQuery(String sql, int fetchSize) throws SQLException {
        logger.debug(sql);
        pst = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        String connName = conn.getClass().getName();
        if (connName.startsWith("com.gbase.jdbc.") || connName.startsWith("com.mysql.")) {
            pst.setFetchSize(Integer.MIN_VALUE);
            logger.debug("ps.setFetchSize(Integer.MIN_VALUE)");
        } else if (connName.startsWith("org.postgresql.")) {
            pst.setFetchSize(10000);
        } else {
            pst.setFetchSize(fetchSize);
        }
        res = pst.executeQuery();
        // DM数据库设置rs的fetchSize不能大于ps的maxRows add by chen.z.b 2017-07-27
        if (conn.getClass().getName().indexOf("dm") >= 0) {
            res.setFetchSize(pst.getMaxRows());
        } else {
            res.setFetchSize(fetchSize);
        }
        ResultSetMetaData resMetaData = res.getMetaData();
        columnMetas = new HashMap<>(resMetaData.getColumnCount());
        for (int i = 1; i <= resMetaData.getColumnCount(); i++) {
            columnMetas.put(i,
                    new ColumnMeta(
                            i,
                            resMetaData.getColumnType(i),
                            resMetaData.getColumnName(i),
                            resMetaData.getColumnTypeName(i)
                    )
            );
        }
    }

    public String createQuerySql(String tableName, NestedCondition condition) {
        if (condition == null) {
            return "select * from " + tableName;
        } else if (StringUtils.isNotBlank(condition.getRawQueryString())) {
            return condition.getRawQueryString();
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        Set<String> columns = condition.getFilterColumns();
        if (columns == null || columns.size() == 0) {
            sql.append("* ");
        } else {
            for (String column : columns) {
                sql.append(column);
                sql.append(",");
            }
            sql.setCharAt(sql.length() - 1, ' ');
        }
        sql.append("from ");
        sql.append(tableName);
        String whereStr = PredicateParser.buildSqlCondition(condition, software);
        if (StringUtils.isNotBlank(whereStr)) {
            sql.append(" where ");
            sql.append(whereStr);
        }
        return sql.toString();
    }

    public void drop(String tableName) {
        String sql = "drop table " + tableName;
        executeDDL(sql);
    }

    public void truncate(String tableName) {
        String sql = "truncate table " + tableName;
        executeDDL(sql);
    }

    private void executeDDL(String ddl) {
        Statement newSmt = null;
        logger.debug(ddl);
        try {
            newSmt = conn.createStatement();
            int i = newSmt.executeUpdate(ddl);
            if (i == 0) {
                logger.debug("DDL执行成功");
            }
        } catch (SQLException e) {
            logger.error("DDL执行失败", e);
            throw new RuntimeException("DDL执行失败", e);
        } finally {
            DbUtil.close(null, newSmt, null);
            end();
        }
    }

    public void commit() {
        try {
            if (pst != null) {
                pst.executeBatch();
            }
            conn.commit();
            logger.debug("事务提交成功！");
        } catch (SQLException e) {
            logger.error("提交失败");
            throw new RuntimeException("提交失败", e);
        } finally {
            end();
        }
    }

    void end() {
        DbUtil.close(res, pst, null);
        res = null;
        pst = null;
        columnMetas = null;
    }


    private DataRowModel getResValue(Map<Integer, ColumnMeta> columnMetas, ResultSet res) throws SQLException {
        DataRowModel dataRowModel = new DataRowModel();
        for (ColumnMeta columnMeta : columnMetas.values()) {
            dataRowModel.addProperties(columnMeta.getColumnName(), TypeTransformer.transform(res.getObject(columnMeta.getColumnName())));
        }
        return dataRowModel;
    }

}

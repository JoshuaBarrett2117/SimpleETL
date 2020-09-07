package com.code.common.dao.core.condition;

import com.code.common.dao.core.param.IncrementParam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Condition implements Serializable {

    /**
     * 是否限制返回条数，默认 -1，即不限制
     */
    private long limit = -1;

    private IncrementParam incrementParam = null;

    /**
     * 优先级最高，如果设置了此列，解析条件会忽略其他列
     * 如果设置，则放弃了迁移其他DAO库实现，绑定了特定库
     * 例如，es的聚合查询
     */
    private String rawQueryString;

    private Map<String, Object> paramMaps = new HashMap<>();

    public void addParams(String key, Object value) {
        this.paramMaps.put(key, value);
    }

    public Map<String, Object> getParamMaps() {
        return paramMaps;
    }

    public void setParamMaps(Map<String, Object> paramMaps) {
        this.paramMaps = paramMaps;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getRawQueryString() {
        return rawQueryString;
    }

    public void setRawQueryString(String rawQueryString) {
        this.rawQueryString = rawQueryString;
    }

    public Condition(IncrementParam incrementParam, String rawQueryString) {
        this.incrementParam = incrementParam;
        this.rawQueryString = rawQueryString;
    }

    public Condition(String rawQueryString) {
        this.rawQueryString = rawQueryString;
    }

    public Condition() {
    }

    public IncrementParam getIncrementParam() {
        return incrementParam;
    }

    public void setIncrementParam(IncrementParam incrementParam) {
        this.incrementParam = incrementParam;
    }
}

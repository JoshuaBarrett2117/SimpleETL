package com.code.common.dao.core.param;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

public class ExpParam implements Serializable {
    private Map<String, Object> params = Maps.newLinkedHashMap();

    public ExpParam() {
    }

    public ExpParam(String key, Object value) {
        this.addParam(key, value);
    }

    public ExpParam addParam(String key, long value, long trimValue) {
        if (value != trimValue) {
            this.params.put(key, value);
        } else {
            this.params.put(key, (Object)null);
        }

        return this;
    }

    public ExpParam addParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public Map<String, Object> param() {
        return this.params;
    }
}

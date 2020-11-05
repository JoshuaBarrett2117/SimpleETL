package com.code.common.dao.core.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author A.Gan
 * time 2019/4/25 18:41
 * description
 */
public class DataRowModel<T> implements Serializable {

    private String id;
    private Map<String, T> properties = new LinkedHashMap<>();
    private DataRowModel metadata;
    private boolean isLast = false;

    public DataRowModel() {
    }

    public DataRowModel(String id, Map<String, T> properties) {
        this.id = id;
        this.properties = properties;
    }

    public void addProperties(Map<String, T> pro) {
        this.properties.putAll(pro);
    }

    public void addProperties(String key, T value) {
        this.properties.put(key, value);
    }

    public void removeProperties(String key) {
        if (null != properties.get(key)) {
            properties.remove(key);
        }
    }

    public <T> T get(String key) {
        return properties.containsKey(key) ? (T) properties.get(key) : null;
    }

    public String getAsString(String key) {
        String s = this.get(key);
        return s == null ? null : s;
    }

    public Integer getAsInt(String key) {
        return Integer.valueOf(this.getAsString(key));
    }

    public Long getAsLong(String key) {
        return Long.valueOf(this.getAsString(key));
    }

    public Number getAsNumber(String key) {
        return (Number) this.get(key);
    }

    public <T> T get(String key, Class<T> tClass) {
        return properties.containsKey(key) ? tClass.cast(this.get(key)) : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, T> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, T> properties) {
        this.properties = properties;
    }

    public DataRowModel getMetadata() {
        return metadata;
    }

    public void setMetadata(DataRowModel metadata) {
        this.metadata = metadata;
    }

    public void setType(String type) {
        createMetadata();
        this.metadata.addProperties("type", type);
    }

    public void setScore(double score) {
        createMetadata();
        this.metadata.addProperties("score", score);
    }

    public void addMetadata(String key, Object o) {
        createMetadata();
        this.metadata.addProperties(key, o);
    }

    private void createMetadata() {
        if (metadata == null) {
            metadata = new DataRowModel();
        }
    }

    public boolean getIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }
}

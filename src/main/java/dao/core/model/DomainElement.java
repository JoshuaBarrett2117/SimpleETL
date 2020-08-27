package dao.core.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author A.Gan
 * time 2019/4/25 18:41
 * description
 */
public class DomainElement implements Serializable {

    private String id;

    private String type;

    private double score;

    private Map<String, Object> properties = Maps.newLinkedHashMap();

    public void addProperties(Map<String, Object> pro) {
        this.properties.putAll(pro);
    }

    public void addProperties(String key, Object value) {
        this.properties.put(key, value);
    }

    public Object get(String key) {
        return properties.containsKey(key) ? properties.get(key) : null;
    }


    /**
     * 获取多值接口
     *
     * @param key
     * @return
     */
    public List<Object> getByMulKey(String key) {
        if (this.properties.containsKey(key)) {
            Object value = this.properties.get(key);
            if (value instanceof List) {
                return (List) value;
            } else {
                return Lists.newArrayList(value);
            }
        }
        return null;
    }


    public DomainElement(String id, String type, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.properties = properties;
    }

    public DomainElement() {
    }

    public void removeProperties(String key) {
        if (null != properties.get(key)) {
            properties.remove(key);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

package com.code.common.dao.core.condition;

import com.code.common.dao.core.query.Compare;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiPredicate;

public class NestedCondition extends Condition {
    protected String key;

    protected Object value;

    protected BiPredicate predicate;

    protected float boost = 1.0f;

    protected Op op = Op.OR;


    protected boolean isSameCondition = Boolean.FALSE;

    /**
     * 内部聚合条件
     */
    private Collection<AggregationCondition> aggregationConditions;

    public Collection<AggregationCondition> getAggregationConditions() {
        return aggregationConditions;
    }

    /**
     * 过滤结果列，默认 size 为空，即不过滤
     */
    protected Set<String> filterColumns = new HashSet<>();

    protected List<NestedCondition> conditions = new LinkedList<>();

    protected Map<String, Object> innerMap = new HashMap<>();

    public static NestedCondition build() {
        return new NestedCondition(null, null, null);
    }

    public NestedCondition(String key, Object value, BiPredicate predicate) {
        this.key = key;
        this.value = value;
        innerMap.put(key, value);
        this.predicate = predicate;
    }

    public NestedCondition addFilterColumns(String columnName) {
        this.filterColumns.add(columnName);
        return this;
    }

    public Set<String> getFilterColumns() {
        return this.filterColumns;
    }

    public NestedCondition addOperator(Op op) {
        Optional.ofNullable(this.getConditions()).ifPresent(c -> {
            c.get(c.size() - 1).setOp(op);
        });
        return this;
    }

    public NestedCondition addParam(String key, Object value, BiPredicate predicate) {
        addParam(new NestedCondition(key, value, predicate));
        return this;
    }

    public NestedCondition addParam(String key, Object value) {
        addParam(key, value, Compare.eq);
        return this;
    }

    public NestedCondition addParam(NestedCondition condition) {
        conditions.add(condition);
        innerMap.put(condition.getKey(), condition.getValue());
        return this;
    }

    public NestedCondition addParam(NestedCondition[] innerCondition) {
        NestedCondition condition = NestedCondition.build();
        for (NestedCondition nestedCondition : innerCondition) {
            condition.addParam(nestedCondition);
        }
        this.conditions.add(condition);
        innerMap.put(condition.getKey(), condition.getValue());
        return this;
    }

    public NestedCondition addBoost(float boost) {
        Optional.ofNullable(this.getConditions()).ifPresent(c -> {
            c.get(c.size() - 1).setBoost(boost);
        });
        return this;
    }

    public void removeCondition(String key) {
        for (NestedCondition condition : conditions) {
            if (key.equals(condition.getKey())) {
                this.getConditions().remove(condition);
                break;
            }
        }
    }

    public boolean isSameCondition() {
        return isSameCondition;
    }

    public void setSameCondition(boolean sameCondition) {
        isSameCondition = sameCondition;
    }

    public float getBoost() {
        return boost;
    }

    private void setBoost(float boost) {
        this.boost = boost;
    }

    public boolean contains(String key) {
        return innerMap.containsKey(key);
    }

    public Object get(String key) {
        return innerMap.get(key);
    }

    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<NestedCondition> getConditions() {
        return conditions;
    }

    public void setNestedConditions(List<NestedCondition> conditions) {
        this.conditions = conditions;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public BiPredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(BiPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * 逻辑操作
     */
    public enum Op {
        AND, OR, NOT
    }

    public enum Order {
        DESC, ASC
    }

    /**
     * 聚合对象
     */
    public static class AggregationCondition implements Serializable {
        /**
         * 字段名
         */
        private String fieldName;
        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 嵌套聚合
         */
        private List<AggregationCondition> aggregationskSons = new ArrayList<>();
        /**
         * 过滤聚合时会用到的查询条件
         */
        private Map<String, NestedCondition> filterCondition = new HashMap<>();
        /**
         * 聚合的设置，部分聚合操作需要设置参数
         */
        private Map<String, Object> setting = new HashMap<>();

        public AggregationCondition() {
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public List<AggregationCondition> getAggregationskSons() {
            return aggregationskSons;
        }

        public Map<String, NestedCondition> getFilterConditions() {
            return filterCondition;
        }

        public void addSon(Collection<AggregationCondition> cdin) {
            aggregationskSons.addAll(cdin);
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public Map<String, Object> getSetting() {
            return setting;
        }

        public void setSetting(Map<String, Object> setting) {
            this.setting = setting;
        }
    }

}

package com.code.pipeline.etl.model;

import java.util.HashMap;
import java.util.Map;

public class MapProxy extends HashMap<String, Object> {
    @Override
    public Object put(String key, Object value) {
        if (!key.contains(".")) {
            return super.put(key, value);
        }
        String[] split = key.split("\\.");
        Map<String, Object> parent;
        if (containsKey(split[0])) {
            parent = (Map) remove(split[0]);
        } else {
            parent = new HashMap<>();
        }
        Map<String, Object> map = parent;
        for (int i = 1; i < split.length - 1; i++) {
            String s = split[i];
            if (map.containsKey(s)) {
                map = (Map) map.get(s);
            } else {
                map.put(s, new HashMap<>());
                map = (Map) map.get(s);
            }
        }
        map.put(split[split.length - 1], value);
        return super.put(split[0], parent);
    }

    public Object get(String key) {
        if (!key.contains(".")) {
            return super.get(key);
        }
        String[] split = key.split("\\.");
        Map<String, Object> map = this;
        for (int i = 0; i < split.length - 1; i++) {
            Object o = map.get(split[i]);
            if (!(o instanceof Map)) {
                return null;
            }
            map = (Map<String, Object>) map.get(split[i]);
        }
        return map.get(split[split.length - 1]);
    }

}
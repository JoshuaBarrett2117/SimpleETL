package com.code.pipeline.worker.renyuan.worker;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class WarpFactory {
    public static IFieldWarp create(String value) {
        if (value != null && value.equalsIgnoreCase("车")) {
            return new CphFieldWarp();
        } else {
            return new DefaultFieldWarp();
        }
    }
}

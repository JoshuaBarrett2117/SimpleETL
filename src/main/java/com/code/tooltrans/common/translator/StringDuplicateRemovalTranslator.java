package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:32
 */
public class StringDuplicateRemovalTranslator implements IIteratorTranslator {

    private String key;

    private Map<String, DataRowModel> temp = new ConcurrentHashMap<>();
    private static final DataRowModel EMPTY_DE = new DataRowModel();
    private AtomicLong count = new AtomicLong();

    public StringDuplicateRemovalTranslator(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {

        return new Iterator<DataRowModel>() {
            String text;
            boolean isOut = true;

            @Override
            public boolean hasNext() {
                if (!isOut) {
                    return true;
                }
                while (iterator.hasNext()) {
                    DataRowModel next = iterator.next();
                    text = next.get(key).toString().trim();
                    if (!temp.containsKey(text)) {
                        next.addProperties(key, text);
                        temp.put(text, next);
                        isOut = false;
                        return true;
                    } else {
                        count.incrementAndGet();
                    }
                }
                text = null;
                temp = null;
                System.out.println(Thread.currentThread().getName() + ":重复了" + count.get() + "个词");
                return false;
            }

            @Override
            public DataRowModel next() {
                isOut = true;
                DataRowModel dataRowModel = temp.get(text);
                temp.put(text, EMPTY_DE);
                return dataRowModel;
            }
        };
    }
}

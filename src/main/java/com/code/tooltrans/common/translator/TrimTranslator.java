package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/10 15:05
 */
public class TrimTranslator implements IIteratorTranslator {
    private String key;

    public TrimTranslator(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
        return new Iterator<DataRowModel>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public DataRowModel next() {
                DataRowModel next = iterator.next();
                next.addProperties(key, next.get(key).toString().replace(" ", ""));
                return next;
            }
        };
    }
}

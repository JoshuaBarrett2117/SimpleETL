package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 18:56
 */
public class ConditionDeleteTranslator implements IIteratorTranslator {
    private Condition condition;
    private String key;

    public ConditionDeleteTranslator(String key, Condition condition) {
        this.condition = condition;
        this.key = key;
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {

        return new Iterator<DataRowModel>() {
            int count = 0;

            @Override
            public boolean hasNext() {
                boolean b = iterator.hasNext();
                if (!b) {
                    System.out.println("条件过滤：删除了" + count + "条数据");
                }
                return b;
            }

            @Override
            public DataRowModel next() {
                DataRowModel next = iterator.next();
                if (condition.isFilter(next, key)) {
                    count++;
                    return null;
                }
                return next;
            }
        };
    }

    public interface Condition {
        boolean isFilter(DataRowModel dataRowModel, String key);
    }
}

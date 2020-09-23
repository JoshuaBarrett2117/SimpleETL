package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 16:14
 */
public class ElementSplitTranslator implements IIteratorTranslator {

    private Func func;

    public ElementSplitTranslator(Func... funcs) {
        this.func = new Func() {
            @Override
            public List<DataRowModel> split(DataRowModel content) {
                Set<DataRowModel> result = new HashSet<>();
                for (Func func1 : funcs) {
                    List<DataRowModel> split = func1.split(content);
                    if (split != null) {
                        result.addAll(split);
                    }
                }
                return new ArrayList<>(result);
            }
        };
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
        return new Iterator<DataRowModel>() {

            Iterator<DataRowModel> temps = Collections.EMPTY_LIST.iterator();

            @Override
            public boolean hasNext() {
                //同时没有的时候，结束迭代
                if (!iterator.hasNext() && !temps.hasNext()) {
                    return false;
                }
                return temps.hasNext() || iterator.hasNext();
            }

            @Override
            public DataRowModel next() {
                if (!temps.hasNext()) {
                    if (iterator.hasNext()) {
                        DataRowModel next = iterator.next();
                        List<DataRowModel> words = func.split(next);
                        temps = words.iterator();
                    }
                }
                if (!temps.hasNext()) {
                    return null;
                }
                return temps.next();
            }
        };
    }

    public interface Func {
        List<DataRowModel> split(DataRowModel content);
    }


}

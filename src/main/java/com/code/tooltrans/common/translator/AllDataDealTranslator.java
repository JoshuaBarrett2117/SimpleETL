package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 全量数据收集器
 *
 * @author liufei
 * @Description
 * @Date 2020/4/24 11:09
 */
public class AllDataDealTranslator implements IIteratorTranslator {
    private Func func;

    public AllDataDealTranslator(Func func) {
        this.func = func;
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
        List<DataRowModel> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return func.collect(result).iterator();
    }

    public interface Func {
        List<DataRowModel> collect(List<DataRowModel> list);
    }
}

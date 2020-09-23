package com.code.tooltrans.common;


import com.code.common.dao.core.model.DataRowModel;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 15:20
 */
public interface IIteratorTranslator {
    Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator);
}

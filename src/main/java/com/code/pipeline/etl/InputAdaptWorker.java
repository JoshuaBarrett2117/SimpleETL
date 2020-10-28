package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataSource;

import java.util.Iterator;

/**
 * 文件名称: InputAdapt.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/10/28     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class InputAdaptWorker extends AbstractInputWorker<DataRowModel> {
    Iterator<DataRowModel> iterator;

    public InputAdaptWorker(String name, IDataSource dataSource, IDataSource.Exp e) {
        super(name);
        iterator = dataSource.iterator(e);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DataRowModel next() {
        return iterator.next();
    }
}

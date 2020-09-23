package com.code.pipeline.etl.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.etl.output.AbstractOutPipe;
import com.code.tooltrans.common.IDataSource;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * 文件名称: OldInputAdapt.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/23     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class OldInputAdapt extends AbstractInputPipe {
    private IDataSource dataSource;
    private Iterator<DataRowModel> modelElementIterator;

    public OldInputAdapt(IDataSource dataSource, IDataSource.Exp exp) {
        this.dataSource = dataSource;
        this.modelElementIterator = dataSource.iterator(exp);
    }

    @Override
    public boolean hasNext() {
        return modelElementIterator.hasNext();
    }

    @Override
    public DataRowModel next() {
        return modelElementIterator.next();
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }
}

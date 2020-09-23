package com.code.pipeline.etl.output;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractPipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件名称: AbstractOutPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/9     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public abstract class AbstractOutPipe extends AbstractPipe<DataRowModel, Void> implements IOutputPipe {
    protected static final int BATCH_SIZE = 5000;
    protected int batchSize = BATCH_SIZE;
    protected List<DataRowModel> rowModels = Collections.synchronizedList(new ArrayList<>());
    protected AtomicInteger currCount = new AtomicInteger(0);
    protected String outputTable;

    public AbstractOutPipe(String outputTable) {
        this.outputTable = outputTable;
    }

    @Override
    public void process(DataRowModel input) throws InterruptedException {
        int size = currCount.incrementAndGet();
        if (size % batchSize == 0) {
            this.out(rowModels);
            rowModels.clear();
        }
        rowModels.add(input);
    }

    @Override
    protected void last() {
        this.out(rowModels);
        rowModels.clear();
    }

    @Override
    public abstract void shutdown(long timeout, TimeUnit unit);
}

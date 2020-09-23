package com.code.pipeline.etl.output;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文件名称: OldOutputAdapt.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/23     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class OldOutputAdapt extends AbstractOutPipe {
    private IDataTarget dataTarget;

    public OldOutputAdapt(String outputTable, IDataTarget dataTarget) {
        super(outputTable);
        this.dataTarget = dataTarget;
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        dataTarget.close();
    }

    @Override
    public void out(List<DataRowModel> out) {
        dataTarget.save(out, outputTable);
    }
}

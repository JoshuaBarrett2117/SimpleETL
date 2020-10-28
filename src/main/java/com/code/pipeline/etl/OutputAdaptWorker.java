package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;

/**
 * 文件名称: OutputAdapt.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/10/28     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class OutputAdaptWorker extends AbstractOutputWorker<DataRowModel> {
    IDataTarget target;
    String targetName;

    public OutputAdaptWorker(String name, IDataTarget target, String targetName) {
        super(name);
        this.target = target;
        this.targetName = targetName;
    }

    @Override
    protected void out(List<DataRowModel> dataRowModels) {
        target.save(dataRowModels, targetName);
    }


}

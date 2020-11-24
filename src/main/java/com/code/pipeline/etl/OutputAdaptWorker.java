package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
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

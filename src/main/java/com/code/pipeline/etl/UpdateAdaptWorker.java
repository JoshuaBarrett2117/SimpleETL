package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;

import java.util.List;

/**
 * 用于更新的输出工作者
 *
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class UpdateAdaptWorker extends AbstractOutputWorker<DataRowModel> {
    IDataTarget target;
    String targetName;
    String idField;

    public UpdateAdaptWorker(String name, IDataTarget target, String targetName, String idField) {
        super(name);
        this.target = target;
        this.targetName = targetName;
        this.idField = idField;
    }

    @Override
    protected void out(List<DataRowModel> dataRowModels) {
        target.update(dataRowModels, targetName, idField);
    }


}

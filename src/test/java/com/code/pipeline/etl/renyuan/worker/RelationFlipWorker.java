package com.code.pipeline.etl.renyuan.worker;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractTransformerWorker;
import com.code.pipeline.core.PipeException;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/5 16:11
 */
public class RelationFlipWorker extends AbstractTransformerWorker<DataRowModel, DataRowModel> {
    private String leftIdField;
    private String rightIdField;
    private String relationTypeField;

    public RelationFlipWorker(String name, String leftIdField, String rightIdField, String relationTypeField) {
        super(name);
        this.leftIdField = leftIdField;
        this.rightIdField = rightIdField;
        this.relationTypeField = relationTypeField;
    }

    @Override
    public DataRowModel doRun(DataRowModel dataRowModel) throws PipeException {
        String leftId = dataRowModel.getAsString(leftIdField);
        String rightId = dataRowModel.getAsString(rightIdField);
        String relationType = dataRowModel.getAsString(relationTypeField);
        dataRowModel.addProperties(leftIdField, rightId);
        dataRowModel.addProperties(rightIdField, leftId);
        dataRowModel.addProperties(relationTypeField, relationType + "(R)");
        return dataRowModel;
    }
}

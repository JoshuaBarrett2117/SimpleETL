package com.code.pipeline.worker.renyuan.worker;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractTransformerWorker;
import com.code.pipeline.core.PipeException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liufei
 * @Description
 * @Date 2020/11/2 14:07
 */
public class ContentCreateWorker extends AbstractTransformerWorker<DataRowModel, DataRowModel> {
    private static final String LEFT = "LEFT";
    private static final String RIGHT = "RIGHT";
    private static final String CONTENT = "CONTENT";
    private static final String SFZHM = "SFZHM";
    private static final String XM = "XM";
    private static final String ID = "ID";
    private static final String CPH = "CPH";
    private static final String JBXX = "JBXX";
    private static final String QGX = "QGX";
    private static final String RGX = "RGX";
    private static final List<String> qgxList;

    static {
        qgxList = new ArrayList<>();
        qgxList.add("配偶");
        qgxList.add("亲子");
        qgxList.add("其他亲属");
    }

    private DataRowModel dataRowModel;
    private StringBuilder contentBuilder;
    private String leftKeyField;
    private String relationTypeField;
    private String rightObjectNameField;
    private String rightObjectIdField;

    private List<FieldMapper> fieldMappers;
    private List<FieldMapper> jbxxFieldMappers;
    private Map<String, List<Map<String, Object>>> qgxMap;
    private Map<String, List<Map<String, Object>>> rgxMap;

    private boolean isInited = false;

    public ContentCreateWorker(String name, String leftKeyField, String relationTypeField, String rightObjectNameField, String rightObjectIdField, List<FieldMapper> fieldMappers, List<FieldMapper> jbxxFieldMappers) {
        super(name);
        this.leftKeyField = leftKeyField;
        this.relationTypeField = relationTypeField;
        this.rightObjectNameField = rightObjectNameField;
        this.rightObjectIdField = rightObjectIdField;
        this.fieldMappers = fieldMappers;
        this.jbxxFieldMappers = jbxxFieldMappers;
    }

    @Override
    public DataRowModel doRun(DataRowModel input) throws PipeException {
        RelationStandarder.standard(input, relationTypeField);
        DataRowModel result = null;
        if (!isInited) {
            initDataRowModel(input);
        }
        //还是这个id
        if (!isInited || !input.getAsString(leftKeyField).equals(dataRowModel.getAsString(ID))) {
            //ID变了，上一条数据应该要输出
            finallyWrap();
            result = this.dataRowModel;
            initDataRowModel(input);
        }
        //包装其他字段
        wrapField(input);
        if (result != null) {
            result.addProperties("QGX", JSONObject.toJSONString(result.get("QGX"), SerializerFeature.DisableCircularReferenceDetect));
            result.addProperties("RGX", JSONObject.toJSONString(result.get("RGX"), SerializerFeature.DisableCircularReferenceDetect));
            result.addProperties("JBXX", JSONObject.toJSONString(result.get("JBXX"), SerializerFeature.DisableCircularReferenceDetect));
        }
        return result;
    }

    private void initDataRowModel(DataRowModel input) {
        this.dataRowModel = new DataRowModel();
        this.contentBuilder = new StringBuilder();
        this.qgxMap = new HashMap<>();
        this.rgxMap = new HashMap<>();
        this.dataRowModel.addProperties(QGX, qgxMap);
        this.dataRowModel.addProperties(RGX, rgxMap);
        this.dataRowModel.addProperties(ID, input.getAsString(leftKeyField));
        this.dataRowModel.setId(input.getAsString(leftKeyField));
        Map<String, Object> jbxx = new HashMap<>();
        this.dataRowModel.addProperties(JBXX, jbxx);
        this.isInited = true;
    }

    private String createJbxxContent(Map<String, Object> jbxx) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : jbxx.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append(" ");
        }
        return sb.toString();
    }

    private void finallyWrap() {
        this.contentBuilder.append(createJbxxContent((Map<String, Object>) this.dataRowModel.get(JBXX)));
        this.dataRowModel.addProperties(CONTENT, contentBuilder.toString());
    }

    private void wrapField(DataRowModel input) {
        contentBuilder.append(wrapContentStr(input));
        for (FieldMapper fieldMapper : fieldMappers) {
            wrapFieldMapper(dataRowModel.getProperties(), input.getProperties(), fieldMapper);
        }
        for (FieldMapper jbxxFieldMapper : jbxxFieldMappers) {
            Map<String, Object> properties = (Map<String, Object>) dataRowModel.get(JBXX);
            wrapFieldMapper(properties, input.getProperties(), jbxxFieldMapper);
        }
        String relationType = input.getAsString(relationTypeField);
        if (qgxList.contains(relationType)) {
            if (qgxMap.containsKey(relationType)) {
                qgxMap.get(relationType).add(createGx(input));
            } else {
                List<Map<String,Object>> qs = new ArrayList<>();
                qs.add(createGx(input));
                qgxMap.put(relationType, qs);
            }
        } else {
            if (rgxMap.containsKey(relationType)) {
                rgxMap.get(relationType).add(createGx(input));
            } else {
                List<Map<String,Object>> qs = new ArrayList<>();
                qs.add(createGx(input));
                rgxMap.put(relationType, qs);
            }
        }
    }

    private Map<String, Object> createGx(DataRowModel input) {
        Map<String, Object> result = new HashMap<>();
        result.put(XM, input.getAsString(rightObjectNameField));
        result.put(SFZHM, input.getAsString(rightObjectIdField));
        return result;
    }

    @NotNull
    private String wrapContentStr(DataRowModel input) {
        StringBuilder sb = new StringBuilder();
        sb.append(input.getAsString(relationTypeField));
        sb.append(":");
        sb.append(input.getAsString(rightObjectNameField));
        sb.append(" ");
        sb.append(input.getAsString(rightObjectIdField));
        sb.append(" ");
        return sb.toString();
    }

    private void wrapFieldMapper(Map<String, Object> properties, Map<String, Object> input, FieldMapper fieldMapper) {
        WarpFactory.create(fieldMapper.type).warp(properties, input, fieldMapper);
    }


}

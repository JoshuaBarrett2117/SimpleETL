package com.code.test;

import com.alibaba.fastjson.JSONObject;
import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.common.utils.StringUtils;
import com.code.pipeline.core.*;
import com.code.pipeline.etl.InputAdaptWorker;
import com.code.pipeline.etl.OutputAdaptWorker;
import com.code.tooltrans.common.HttpRequest;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JwqDemo {
    private static final Logger logger = LoggerFactory.getLogger(JwqDemo.class);
    HttpRequest httpRequest = new HttpRequest();

    public static void main(String[] args) {
        new JwqDemo().test();
    }

    public void test() {

        final SimplePipeline<Void, DataRowModel> pipeline =
                new SimplePipeline<>("pipeline");

        Pipe<Void, DataRowModel> inputPipe = new InputMultipleWorkerPipe<>(
                new ArrayBlockingQueue<>(2000),
                "inputPipe",
                getInputPipeWorker1()
                , getInputPipeWorker2()
//                , getInputPipeWorker3()
//                , getInputPipeWorker4()
        );
        pipeline.addPipe(inputPipe);
        Pipe<DataRowModel, DataRowModel> pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>(
                new ArrayBlockingQueue<>(2000),
                "pipe1",
                newWorker("pipe1-1")
                , newWorker("pipe1-2")
                , newWorker("pipe1-3")
                , newWorker("pipe1-4")
//                , newWorker("pipe1-5")
//                , newWorker("pipe1-6")
//                , newWorker("pipe1-7")
//                , newWorker("pipe1-8")
        ) {

        };
        pipeline.addPipe(pipe);

        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                new ArrayBlockingQueue<>(2000),
                "outputPipe",
                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
//                , new OutputAdaptWorker("outputPipeWorker2", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
//                , new OutputAdaptWorker("outputPipeWorker3", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
//                , new OutputAdaptWorker("outputPipeWorker4", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
//                , new OutputAdaptWorker("outputPipeWorker5", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
//                , new OutputAdaptWorker("outputPipeWorker6", getDataTarget(), "TJ_T_RH_WZ_DX_DTDLWZ_DI_N1126")
        ) {
        };

        pipeline.addPipe(outputPipe);

        pipeline.init(pipeline.newDefaultPipelineContext());

        try {
            pipeline.process(null);
        } catch (IllegalStateException e) {
            ;
        } catch (InterruptedException e) {
            ;
        }
        pipeline.shutdown(100, TimeUnit.MILLISECONDS);
        logger.info("结束");

    }

    private InputAdaptWorker getInputPipeWorker1() {
        return new InputAdaptWorker("inputPipeWorker1", getDataSource(),
                new IDataSource.Exp("SELECT * FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 10000000) WHERE \"NAVICAT_ROWNUM\" > 0"));
//                new IDataSource.Exp("SELECT * FROM TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117"));
    }

    private InputAdaptWorker getInputPipeWorker2() {
        return new InputAdaptWorker("inputPipeWorker2", getDataSource(),
                new IDataSource.Exp("SELECT * FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 25000000) WHERE \"NAVICAT_ROWNUM\" > 10000000"));
    }

    private InputAdaptWorker getInputPipeWorker3() {
        return new InputAdaptWorker("inputPipeWorker3", getDataSource(),
                new IDataSource.Exp("SELECT * FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 20000000) WHERE \"NAVICAT_ROWNUM\" > 15000000"));
    }

    private InputAdaptWorker getInputPipeWorker4() {
        return new InputAdaptWorker("inputPipeWorker4", getDataSource(),
                new IDataSource.Exp("SELECT * FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1117\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 25000000) WHERE \"NAVICAT_ROWNUM\" > 20000000"));
    }

    private IDataSource getDataSource() {
        Properties properties2 = new Properties();
        properties2.setProperty("driverName", "oracle.jdbc.OracleDriver");
        properties2.setProperty("url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
        properties2.setProperty("user", "iof");
        properties2.setProperty("password", "dragon");
        RdbDataSource dataSource = new RdbDataSource(properties2);
        return new RdbSource(dataSource.getConnection(), new Software("oracle"));
    }

    private IDataTarget getDataTarget() {
        Properties properties2 = new Properties();
        properties2.setProperty("driverName", "oracle.jdbc.OracleDriver");
        properties2.setProperty("url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
        properties2.setProperty("user", "iof");
        properties2.setProperty("password", "dragon");
        RdbDataSource dataSource = new RdbDataSource(properties2);
        return new RdbTarget(dataSource.getConnection(), new Software("oracle"));
    }

    private AbstractTransformerWorker<DataRowModel, DataRowModel> newWorker(String s) {
        return new AbstractTransformerWorker<DataRowModel, DataRowModel>(s) {
            @Override
            public DataRowModel doRun(DataRowModel input) throws PipeException {
                String bzdm = input.getAsString("BZDM");
                String jd = input.getAsString("JD");
                input.removeProperties("NAVICAT_ROWNUM");
                input.removeProperties("NAVICAT_ROWID");
                if (StringUtils.isBlank(jd)) {
                    String json = getJwd(bzdm);
                    if (json != null) {
                        JSONObject response = JSONObject.parseObject(json);
                        Integer status = response.getInteger("status");
                        if (status == 0) {
                            JSONObject result = response.getJSONObject("result");
                            JSONObject location = result.getJSONObject("location");
                            input.addProperties("JD", location.getDouble("lng"));
                            input.addProperties("WD", location.getDouble("lat"));
                        }
                    }
                }
                return input;
            }

            private String getJwd(String bzdm) {
                try {
                    int statusCode = 0;
                    String json;
                    do {
                        CloseableHttpResponse closeableHttpResponse = httpRequest.get("https://api.map.baidu.com/geocoder/v2/?address=" + bzdm + "&output=json&ak=gQsCAgCrWsuN99ggSIjGn5nO", null);
                        HttpEntity entity = closeableHttpResponse.getEntity();
                        json = EntityUtils.toString(entity);
                        statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
                    } while (statusCode != 200);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


}
package com.code.pipeline.worker.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.pipeline.core.AbstractTransformerMultipleWorkerPipe;
import com.code.pipeline.core.InputMultipleWorkerPipe;
import com.code.pipeline.core.Pipe;
import com.code.pipeline.core.SimplePipeline;
import com.code.pipeline.worker.InputAdaptWorker;
import com.code.pipeline.worker.OutputAdaptWorker;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.elasticsearch.ElasticsearchTarget;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RdbToEsDemo {
    private static final Logger logger = LoggerFactory.getLogger(RdbToEsDemo.class);

    @Test
    public void test() throws InterruptedException {

        final SimplePipeline<Void, DataRowModel> pipeline =
                new SimplePipeline<>("pipeline");

        Pipe<Void, DataRowModel> inputPipe = new InputMultipleWorkerPipe<>(
                new ArrayBlockingQueue<>(2000),
                "inputPipe",
                getInputPipeWorker1()
        );
        pipeline.addPipe(inputPipe);

        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                new ArrayBlockingQueue<>(2000),
                "outputPipe",
                AbstractTransformerMultipleWorkerPipe.<OutputAdaptWorker>createArray(
                        index -> new OutputAdaptWorker("outputPipeWorker" + index, getDataTarget(), "t_rh_wz_dx_dtdlwz_di")
                        , OutputAdaptWorker.class
                        , 16
                )
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
                new IDataSource.Exp("SELECT ZJ_ID,SMC,SHIMC,QXMC,XZJDMC,'' AS XZQHDM,FJDDMC,CJ_IDS,CJGX,JD,WD,BZDM FROM TJ_T_RH_WZ_DX_DTDLWZ_DI_N1208"));
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
        return new ElasticsearchTarget("192.168.125.5", "9400");
//        return new ConsoleTarget();
    }

}
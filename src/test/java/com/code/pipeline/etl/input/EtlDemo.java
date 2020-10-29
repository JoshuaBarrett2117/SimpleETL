package com.code.pipeline.etl.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.pipeline.core.*;
import com.code.pipeline.etl.InputAdaptWorker;
import com.code.pipeline.etl.OutputAdaptWorker;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EtlDemo {
    private static final Logger logger = LoggerFactory.getLogger(EtlDemo.class);

    @Test
    public void test() throws InterruptedException {
        final ThreadPoolExecutor executorSerivce =
                new ThreadPoolExecutor(1, 3,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final SimplePipeline<Void, DataRowModel> pipeline =
                new SimplePipeline<>("pipeline");

        Pipe<Void, DataRowModel> inputPipe =
                new AbstractInputMultipleWorkerPipe<DataRowModel>("inputPipe",
                        getInputPipeWorker1(), getInputPipeWorker2()
                ) {
                };
        pipeline.addPipe(inputPipe);
//
//        Pipe<DataRowModel, DataRowModel> pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>("pipe1",
//                newWorker("pipe1-1")
//                , newWorker("pipe1-2")
//                , newWorker("pipe1-3")
//                , newWorker("pipe1-4")
//        ) {
//
//        };
//
//        pipeline.addPipe(pipe);
//
//        pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>("pipe2",
//                newWorker("pipe2-1")
//                , newWorker("pipe2-2")
//                , newWorker("pipe2-3")
//                , newWorker("pipe2-4")
//        ) {
//
//        };
//
//        pipeline.addPipe(pipe);
//
//        pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>("pipe3",
//                newWorker("pipe3-1")
//                , newWorker("pipe3-2")
//                , newWorker("pipe3-3")
//                , newWorker("pipe3-4")
//        ) {
//
//        };
//
//        pipeline.addPipe(pipe);

        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                "outputPipe",
                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), "TJ_GD_STATISTICS_XX2"),
                new OutputAdaptWorker("outputPipeWorker2", getDataTarget(), "TJ_GD_STATISTICS_XX2"),
                new OutputAdaptWorker("outputPipeWorker3", getDataTarget(), "TJ_GD_STATISTICS_XX2"),
                new OutputAdaptWorker("outputPipeWorker4", getDataTarget(), "TJ_GD_STATISTICS_XX2"),
                new OutputAdaptWorker("outputPipeWorker5", getDataTarget(), "TJ_GD_STATISTICS_XX2"),
                new OutputAdaptWorker("outputPipeWorker6", getDataTarget(), "TJ_GD_STATISTICS_XX2")
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
        pipeline.shutdown(1, TimeUnit.MILLISECONDS);
        logger.info("结束");

    }

    @NotNull
    private InputAdaptWorker getInputPipeWorker1() {
        return new InputAdaptWorker("inputPipeWorker1", getDataSource(),
                new IDataSource.Exp("SELECT ZJ_ID FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1026\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1026\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 10000000) WHERE \"NAVICAT_ROWNUM\" > 0"));
//                new IDataSource.Exp("SELECT ZJ_ID FROM TJ_T_RH_WZ_DX_DTDLWZ_DI_N1026"));
    }

    @NotNull
    private InputAdaptWorker getInputPipeWorker2() {
        return new InputAdaptWorker("inputPipeWorker2", getDataSource(),
                new IDataSource.Exp("SELECT ZJ_ID FROM (SELECT \"NAVICAT_TABLE\".*, ROWNUM \"NAVICAT_ROWNUM\" FROM (SELECT \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1026\".*,ROWID \"NAVICAT_ROWID\" FROM \"IOF\".\"TJ_T_RH_WZ_DX_DTDLWZ_DI_N1026\") \"NAVICAT_TABLE\" WHERE ROWNUM <= 20000000) WHERE \"NAVICAT_ROWNUM\" > 10000000"));
    }

    private IDataSource.Exp getExp() {
        return new IDataSource.Exp("select * from TJ_GD_STATISTICS_XX");
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

    @NotNull
    private AbstractTransformerWorker<DataRowModel, DataRowModel> newWorker(String s) {
        return new AbstractTransformerWorker<DataRowModel, DataRowModel>(s) {
            @Override
            public DataRowModel doRun(DataRowModel input) throws PipeException {
                String key = "ZJ_ID";
                String text = input.getAsString(key);
//                String result = text + "->[" + s + "," + Thread.currentThread().getName() + "]";
//                logger.info(result);
//                try {
//                    Thread.sleep(new Random().nextInt(100));
//                } catch (InterruptedException e) {
//                    ;
//                }
                input.addProperties(key, text);
                return input;
            }
        };
    }


}
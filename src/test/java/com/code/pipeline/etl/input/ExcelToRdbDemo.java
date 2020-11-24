package com.code.pipeline.etl.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.pipeline.core.AbstractTransformerMultipleWorkerPipe;
import com.code.pipeline.core.InputMultipleWorkerPipe;
import com.code.pipeline.core.Pipe;
import com.code.pipeline.core.SimplePipeline;
import com.code.pipeline.etl.InputAdaptWorker;
import com.code.pipeline.etl.OutputAdaptWorker;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.excel.ExcelFileSourceBuilder;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ExcelToRdbDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelToRdbDemo.class);

    @Test
    public void test() throws InterruptedException {

        final SimplePipeline<Void, DataRowModel> pipeline =
                new SimplePipeline<>("pipeline");

        Pipe<Void, DataRowModel> inputPipe = new InputMultipleWorkerPipe<>(
                new ArrayBlockingQueue<>(2000),
                "inputPipe",
                getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk.xlsx", 1)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk2.xlsx", 2)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk3.xlsx", 3)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk4.xlsx", 4)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk5.xlsx", 5)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk6.xlsx", 6)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk7.xlsx", 7)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk8.xlsx", 8)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk9.xlsx", 9)
                , getInputPipeWorker("C:\\Users\\joshua\\Desktop\\地址映射\\czrk10.xlsx", 10)
        );
        pipeline.addPipe(inputPipe);

        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                new ArrayBlockingQueue<>(2000),
                "outputPipe",
                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), "TJ_CZRK")
                , new OutputAdaptWorker("outputPipeWorker2", getDataTarget(), "TJ_CZRK")
                , new OutputAdaptWorker("outputPipeWorker3", getDataTarget(), "TJ_CZRK")
                , new OutputAdaptWorker("outputPipeWorker4", getDataTarget(), "TJ_CZRK")
                , new OutputAdaptWorker("outputPipeWorker5", getDataTarget(), "TJ_CZRK")
                , new OutputAdaptWorker("outputPipeWorker6", getDataTarget(), "TJ_CZRK")
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

    private InputAdaptWorker getInputPipeWorker(String filePath, int i) {
        return new InputAdaptWorker("inputPipeWorker" + i,
                ExcelFileSourceBuilder
                        .anExcelFileSource(filePath)
                        .columnNames(Arrays.asList("S_FJDM", "S_FJ_DZBM", "S_FJ_DZMC", "S_JLXY_DZBM", "S_JLXY_DZMC", "S_JWQDM", "S_JZW_DZBM", "S_LAT", "S_LON", "S_QHNXXDZ", "S_SSFJMC", "S_SSJWQMC", "S_SSQX_DZBM", "S_SSQX_DZMC", "S_XQ_DZBM", "S_XQ_DZMC", "S_ZXDHZB", "S_ZXDZZB"))
                        .isFirstRowsAreColumns(true)
                        .sheetIndices(Arrays.asList(1))
                        .build(),
                new IDataSource.Exp(null));
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

}
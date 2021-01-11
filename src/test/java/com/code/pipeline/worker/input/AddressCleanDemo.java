//package com.code.pipeline.worker.input;
//
//import com.code.common.dao.core.model.DataRowModel;
//import com.code.common.dao.jdbc.operator.Software;
//import com.code.datacleaning.IAddressDataCleaning;
//import com.code.datacleaning.impl.AddressDataCleaningImpl;
//import com.code.pipeline.core.*;
//import com.code.pipeline.worker.InputAdaptWorker;
//import com.code.pipeline.worker.OutputAdaptWorker;
//import com.code.tooltrans.common.HttpRequest;
//import com.code.tooltrans.common.IDataSource;
//import com.code.tooltrans.common.IDataTarget;
//import com.code.tooltrans.common.RdbDataSource;
//import com.code.tooltrans.common.source.rdb.RdbSource;
//import com.code.tooltrans.common.target.elasticsearch.ElasticsearchTarget;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.Properties;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//public class AddressCleanDemo {
//    private static final Logger logger = LoggerFactory.getLogger(AddressCleanDemo.class);
//    HttpRequest httpRequest = new HttpRequest();
//
//    @Test
//    public void test() throws InterruptedException {
//
//        final SimplePipeline<Void, DataRowModel> pipeline =
//                new SimplePipeline<>("pipeline");
//
//        Pipe<Void, DataRowModel> inputPipe = new InputMultipleWorkerPipe<>(
//                new ArrayBlockingQueue<>(2000),
//                "inputPipe",
//                getInputPipeWorker1()
//        );
//        pipeline.addPipe(inputPipe);
//        Pipe<DataRowModel, List<DataRowModel>> pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, List<DataRowModel>>(
//                new ArrayBlockingQueue<>(2000),
//                "pipe1",
//                newWorker("pipe1-1")
//                , newWorker("pipe1-2")
//                , newWorker("pipe1-3")
//                , newWorker("pipe1-4")
//        ) {
//
//        };
//        pipeline.addPipe(pipe);
//
//        Pipe<List<DataRowModel>, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<List<DataRowModel>, Void>(
//                new ArrayBlockingQueue<>(2000),
//                "outputPipe",
//                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), "t_rh_wz_dx_dtdlwz_di_201217_gd")
//        ) {
//        };
//
//        pipeline.addPipe(outputPipe);
//
//        pipeline.init(pipeline.newDefaultPipelineContext());
//
//        try {
//            pipeline.process(null);
//        } catch (IllegalStateException e) {
//            ;
//        } catch (InterruptedException e) {
//            ;
//        }
//        pipeline.shutdown(100, TimeUnit.MILLISECONDS);
//        logger.info("结束");
//
//    }
//
//    private InputAdaptWorker getInputPipeWorker1() {
//        return new InputAdaptWorker("inputPipeWorker1", getDataSource(),
//                new IDataSource.Exp("SELECT * FROM TJ_GD_DLWZ"));
//    }
//
//    private IDataSource getDataSource() {
//        Properties properties2 = new Properties();
//        properties2.setProperty("driverName", "oracle.jdbc.OracleDriver");
//        properties2.setProperty("url", "jdbc:oracle:thin:@192.168.123.25:1521:orcl");
//        properties2.setProperty("user", "iof");
//        properties2.setProperty("password", "dragon");
//        RdbDataSource dataSource = new RdbDataSource(properties2);
//        return new RdbSource(dataSource.getConnection(), new Software("oracle"));
//    }
//
//    private IDataTarget getDataTarget() {
//        return new ElasticsearchTarget("192.168.125.5", "9400");
//    }
//
//    @NotNull
//    private AbstractTransformerWorker<DataRowModel, List<DataRowModel>> newWorker(String s) {
//        return new AbstractTransformerWorker<DataRowModel, List<DataRowModel>>(s) {
//            @Override
//            public List<DataRowModel> doRun(DataRowModel input) throws PipeException {
//                IAddressDataCleaning dataCleaning = new AddressDataCleaningImpl();
//                List<com.code.common.vo.DataRowModel<String>> clean = dataCleaning.clean(input.getAsString("ADDRESS"), null, input.getAsString("CITY_NAME"), input.getAsString("ADNAME"), null);
//                List<DataRowModel> collect = clean.stream().map((r) -> {
//                    DataRowModel rr = new DataRowModel();
//                    rr.setProperties(r.getProperties());
//                    return rr;
//                }).collect(Collectors.toList());
//                return collect;
//            }
//
//        };
//    }
//
//
//}
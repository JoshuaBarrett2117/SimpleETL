package com.code.test;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.common.utils.StringUtils;
import com.code.jwq.service.IPoliceUnitService;
import com.code.jwq.service.impl.PoliceUnitServiceImpl;
import com.code.jwq.vo.PoliceUnitVo;
import com.code.pipeline.core.*;
import com.code.pipeline.etl.InputAdaptWorker;
import com.code.pipeline.etl.UpdateAdaptWorker;
import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JwqDemo {
    private static final Logger logger = LoggerFactory.getLogger(JwqDemo.class);
    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    AbstractMain.class.getResourceAsStream("/geo_mapper_config.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        );
        pipeline.addPipe(inputPipe);
        Pipe<DataRowModel, DataRowModel> pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>(
                new ArrayBlockingQueue<>(2000),
                "pipe1",
                newWorker("pipe1-1")
                , newWorker("pipe1-2")
                , newWorker("pipe1-3")
                , newWorker("pipe1-4")
        ) {

        };
        pipeline.addPipe(pipe);

        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                new ArrayBlockingQueue<>(2000),
                "outputPipe",
                new UpdateAdaptWorker("outputPipeWorker1", getDataTarget(), properties.getProperty("table_name"), properties.getProperty("id_field"))
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
                new IDataSource.Exp("SELECT * FROM " + properties.getProperty("table_name")));
    }


    private IDataSource getDataSource() {
        Properties properties2 = new Properties();
        properties2.setProperty("driverName", properties.getProperty("db.driverName"));
        properties2.setProperty("url", properties.getProperty("db.url"));
        properties2.setProperty("user", properties.getProperty("db.userName"));
        properties2.setProperty("password", properties.getProperty("db.password"));
        RdbDataSource dataSource = new RdbDataSource(properties2);
        return new RdbSource(dataSource.getConnection(), new Software("PostgreSql"));
    }

    private IDataTarget getDataTarget() {
        Properties properties2 = new Properties();
        properties2.setProperty("driverName", properties.getProperty("db.driverName"));
        properties2.setProperty("url", properties.getProperty("db.url"));
        properties2.setProperty("user", properties.getProperty("db.userName"));
        properties2.setProperty("password", properties.getProperty("db.password"));
        RdbDataSource dataSource = new RdbDataSource(properties2);
        return new RdbTarget(dataSource.getConnection(), new Software("PostgreSql"));
    }

    private AbstractTransformerWorker<DataRowModel, DataRowModel> newWorker(String s) {
        IPoliceUnitService service = new PoliceUnitServiceImpl();
        return new AbstractTransformerWorker<DataRowModel, DataRowModel>(s) {
            @Override
            public DataRowModel doRun(DataRowModel input) throws PipeException {
                DataRowModel updateRow = new DataRowModel();
                Number jdField = input.getAsNumber(properties.getProperty("jd_field"));
                Number wdField = input.getAsNumber(properties.getProperty("wd_field"));
                if (jdField == null || wdField == null) {
                    return null;
                }
                PoliceUnitVo vo = service.computeByJWD(jdField.toString(), wdField.toString(), properties.getProperty("level"));
                if (vo == null) {
                    return null;
                }
                updateRow.setId(input.getAsString(properties.getProperty("id_field")));
                updateRow.addProperties(properties.getProperty("jwcmc_field"), vo.getUnitName());
                updateRow.addProperties(properties.getProperty("jwcdm_field"), vo.getUnitId());
                return updateRow;
            }
        };
    }


}
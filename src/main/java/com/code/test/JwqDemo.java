package com.code.test;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.jwq.manage.impl.PoliceUnitManageImpl;
import com.code.jwq.service.IPoliceUnitService;
import com.code.jwq.service.impl.PoliceUnitServiceImpl;
import com.code.jwq.vo.PoliceUnitVo;
import com.code.pipeline.core.*;
import com.code.pipeline.etl.InputAdaptWorker;
import com.code.pipeline.etl.OutputAdaptWorker;
import com.code.pipeline.etl.UpdateAdaptWorker;
import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.rdb.RdbTarget;
import com.code.tooltrans.common.target.text.CsvTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        new PoliceUnitManageImpl();
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
                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), properties.getProperty("table_name"))
                , new OutputAdaptWorker("outputPipeWorker2", getDataTarget(), properties.getProperty("table_name"))
                , new OutputAdaptWorker("outputPipeWorker3", getDataTarget(), properties.getProperty("table_name"))
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
        System.exit(0);
    }

    private InputAdaptWorker getInputPipeWorker1() {
        return new InputAdaptWorker("inputPipeWorker1", getDataSource(),
                new IDataSource.Exp("SELECT * FROM " + properties.getProperty("table_name")
                        + " where " + properties.getProperty("jd_field") + " is not null"
                        + " and " + properties.getProperty("jwcmc_field") + " is null"));
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
        return new CsvTarget("./update.csv", Arrays.asList(properties.getProperty("jwcmc_field"), properties.getProperty("jwcdm_field"), properties.getProperty("id_field")));
    }

    private Pattern numberPattern = Pattern.compile("[0-9]+\\.*[0-9]*");

    private AbstractTransformerWorker<DataRowModel, DataRowModel> newWorker(String s) {
        IPoliceUnitService service = new PoliceUnitServiceImpl();
        return new AbstractTransformerWorker<DataRowModel, DataRowModel>(s) {
            @Override
            public DataRowModel doRun(DataRowModel input) throws PipeException {
                DataRowModel updateRow = new DataRowModel();
                Object jdField = input.get(properties.getProperty("jd_field"));
                Object wdField = input.get(properties.getProperty("wd_field"));
                if (jdField == null || wdField == null) {
                    logger.error("[{}]没有经纬度字段", input.getAsString(properties.getProperty("id_field")));
                    return null;
                }
                PoliceUnitVo vo = service.computeByJWD(getNumberStr(jdField.toString()), getNumberStr(wdField.toString()), properties.getProperty("level"));
                if (vo == null) {
                    logger.error("[{},{},{}]映射失败，映射的结果为null", input.getAsString(properties.getProperty("id_field")), jdField.toString(), wdField.toString());
                    return null;
                }
                logger.trace("映射成功：[{}],[{}]", vo.getUnitId(), vo.getUnitName());
                updateRow.setId(input.getAsString(properties.getProperty("id_field")));
                updateRow.addProperties(properties.getProperty("jwcmc_field"), vo.getUnitName());
                updateRow.addProperties(properties.getProperty("jwcdm_field"), vo.getUnitId());
                updateRow.addProperties(properties.getProperty("id_field"), updateRow.getId());
                return updateRow;
            }

            private String getNumberStr(String toString) {
                Matcher matcher = numberPattern.matcher(toString);
                if (!matcher.find()) {
                    return "0";
                }
                String[] split = toString.split("\\.");
                if (split.length <= 2) {
                    return toString;
                } else {
                    return split[0] + "." + split[1];
                }
            }
        };
    }


}
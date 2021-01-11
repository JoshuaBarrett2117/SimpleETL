package com.code.pipeline.worker.renyuan;

import com.code.common.dao.core.model.DataRowModel;
import com.code.common.dao.jdbc.operator.Software;
import com.code.pipeline.core.*;
import com.code.pipeline.worker.InputAdaptWorker;
import com.code.pipeline.worker.OutputAdaptWorker;
import com.code.pipeline.worker.renyuan.worker.ContentCreateWorker;
import com.code.pipeline.worker.renyuan.worker.FieldMapper;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.RdbDataSource;
import com.code.tooltrans.common.source.rdb.RdbSource;
import com.code.tooltrans.common.target.elasticsearch.ElasticsearchTarget;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RenYuanDemo {
    private static final Logger logger = LoggerFactory.getLogger(RenYuanDemo.class);

    @Test
    public void test() throws InterruptedException {
        //创建一个管道线流程
        final SimplePipeline<Void, DataRowModel> pipeline =
                new SimplePipeline<>("pipeline");
        //构建输入管道
        Pipe<Void, DataRowModel> inputPipe = new InputMultipleWorkerPipe<>(
                new ArrayBlockingQueue<>(2000),
                "inputPipe",
                //添加数据搬运工
                getInputPipeWorker1()
        );
        pipeline.addPipe(inputPipe);
        //构建转换管道
        Pipe<DataRowModel, DataRowModel> pipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, DataRowModel>(
                new ArrayBlockingQueue<>(2000),
                "pipe1",
                //添加一个工人
                newWorker("ContentCreateWorker")
        ) {

        };

        pipeline.addPipe(pipe);
        //构建输出管道
        Pipe<DataRowModel, Void> outputPipe = new AbstractTransformerMultipleWorkerPipe<DataRowModel, Void>(
                new ArrayBlockingQueue<>(2000),
                "outputPipe",
                //添加若干个工人
                new OutputAdaptWorker("outputPipeWorker1", getDataTarget(), "t_renyuan_diku")
                , new OutputAdaptWorker("outputPipeWorker2", getDataTarget(), "t_renyuan_diku")
                , new OutputAdaptWorker("outputPipeWorker3", getDataTarget(), "t_renyuan_diku")
                , new OutputAdaptWorker("outputPipeWorker4", getDataTarget(), "t_renyuan_diku")
                , new OutputAdaptWorker("outputPipeWorker5", getDataTarget(), "t_renyuan_diku")
                , new OutputAdaptWorker("outputPipeWorker6", getDataTarget(), "t_renyuan_diku")
        ) {
        };

        pipeline.addPipe(outputPipe);
        //管道线初始化
        pipeline.init(pipeline.newDefaultPipelineContext());

        //执行管道线
        try {
            pipeline.process(null);
        } catch (IllegalStateException e) {
            ;
        } catch (InterruptedException e) {
            ;
        }

        //关闭管道线
        pipeline.shutdown(100, TimeUnit.MILLISECONDS);
        logger.info("结束");

    }

    private InputAdaptWorker getInputPipeWorker1() {
        return new InputAdaptWorker("inputPipeWorker1", getDataSource(),
                new IDataSource.Exp("SELECT * FROM (\n" +
                        "SELECT LEFT.ID LEFT_ID,LEFT.TYPE LEFT_TYPE,LEFT.SX LEFT_SX,LEFT.SFZH LEFT_OBJ_ID,LEFT.XM,\n" +
                        "RELATION.RELATION_TYPE,\n" +
                        "RIGHT.ID RIGHT_ID,RIGHT.TYPE RIGHT_TYPE,RIGHT.SX RIGHT_SX ,RIGHT.SFZH RIGHT_OBJ_ID,RIGHT.XM RIGHT_NAME\n" +
                        "FROM LF_TEST_RELATION RELATION \n" +
                        "LEFT JOIN LF_TEST_RENYUAN LEFT ON LEFT.ID = RELATION.LEFT_ID \n" +
                        "LEFT JOIN LF_TEST_RENYUAN RIGHT ON RIGHT.ID = RELATION.RIGHT_ID  \n" +
                        "WHERE LEFT.ID IS NOT NULL and right.type = '人' \n" +
                        "UNION ALL \n" +
                        "SELECT LEFT.ID LEFT_ID,LEFT.TYPE LEFT_TYPE,LEFT.SX LEFT_SX,LEFT.SFZH LEFT_OBJ_ID,LEFT.XM,\n" +
                        "RELATION.RELATION_TYPE,\n" +
                        "RIGHT.ID RIGHT_ID,RIGHT.TYPE RIGHT_TYPE,RIGHT.SX RIGHT_SX ,RIGHT.CPH RIGHT_OBJ_ID,RIGHT.CPH RIGHT_NAME\n" +
                        "FROM LF_TEST_RELATION RELATION \n" +
                        "LEFT JOIN LF_TEST_RENYUAN LEFT ON LEFT.ID = RELATION.LEFT_ID \n" +
                        "LEFT JOIN LF_TEST_CHE RIGHT ON RIGHT.ID = RELATION.RIGHT_ID  \n" +
                        "WHERE LEFT.ID IS NOT NULL and right.type = '车'\n" +
                        ")\n" +
                        "order by left_id "));
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
//        return new CsvTarget("C:\\Users\\joshua\\Desktop\\spark\\test1.txt", Arrays.asList(
//                "LEFT_ID", "LEFT_TYPE", "LEFT_SX", "LEFT_OBJ_ID", "RIGHT_NAME",
//                "RELATION_TYPE",
//                "RIGHT_ID", "RIGHT_TYPE", "RIGHT_SX", "RIGHT_OBJ_ID", "RIGHT_NAME"));
    }

    @NotNull
    private AbstractTransformerWorker<DataRowModel, DataRowModel> newWorker(String s) {
        return new ContentCreateWorker(s,
                "LEFT_ID", "RELATION_TYPE", "RIGHT_NAME", "RIGHT_OBJ_ID",
                Arrays.asList(
                        new FieldMapper("车", "RIGHT_NAME", "CPH")
                        , new FieldMapper("LEFT_OBJ_ID", "SFZHM")
                        , new FieldMapper("XM", "XM")
                ),
                Arrays.asList(
                        new FieldMapper("车", "RIGHT_NAME", "车牌号")
                        , new FieldMapper("LEFT_SX", "属性")
                        , new FieldMapper("LEFT_OBJ_ID", "身份证号码")
                        , new FieldMapper("XM", "姓名")
                )
        );
    }
}
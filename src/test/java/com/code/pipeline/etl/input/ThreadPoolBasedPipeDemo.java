package com.code.pipeline.etl.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.PipeException;
import com.code.pipeline.core.SimplePipeline;
import com.code.pipeline.etl.output.ConsoleOutputPipe;
import com.code.pipeline.etl.output.OldOutputAdapt;
import com.code.pipeline.etl.transformer.AbstractTransformerPipe;
import com.code.tooltrans.common.source.text.TextFileSource;
import com.code.tooltrans.common.target.text.TextFileTarget;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBasedPipeDemo {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolBasedPipeDemo.class);

    @Test
    public void test() throws InterruptedException {
        /*
         * 创建线程池
         */
        final ThreadPoolExecutor executorService =
                new ThreadPoolExecutor(1,
                        Runtime.getRuntime().availableProcessors() * 2,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        (r) -> {
                            Thread t = new Thread(r);
                            t.setDaemon(true);
                            return t;
                        }, new ThreadPoolExecutor.CallerRunsPolicy());
        /*
         * 创建管道线对象
         */
        final SimplePipeline<String, String> pipeline = new SimplePipeline<String, String>();
        //创建一个输入管道
        AbstractInputPipe inputPipe = new AbstractInputPipe() {
            int count = 5001;
            int ii = 1;

            @Override
            public boolean hasNext() {
                return ii <= count;
            }

            @Override
            public DataRowModel next() {
                DataRowModel dataRowModel = new DataRowModel();
                dataRowModel.addProperties("food", "Food-" + ii);
                ii++;
                return dataRowModel;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

            }
        };
        pipeline.addPipe(inputPipe);
        /*
         * 创建第一条处理管道
         */
        AbstractTransformerPipe pipe = new AbstractTransformerPipe() {
            Random random = new Random(System.currentTimeMillis());

            @Override
            protected DataRowModel doProcess(DataRowModel input) throws PipeException {
                String food = input.getAsString("food");
                String result = food + "->[pipe1," + Thread.currentThread().getName() + "]";
                input.addProperties("food", result);
                logger.info(result);
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return input;
            }

            @Override
            protected void last() {

            }
        };
        /*
         * 将第一条管道加入线程池
         */
        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        /*
         * 创建第二条
         */
        pipe = new AbstractTransformerPipe() {
            @Override
            protected DataRowModel doProcess(DataRowModel input) throws PipeException {
                String food = input.getAsString("food");
                String result = food + "->[pipe2," + Thread.currentThread().getName() + "]";
                input.addProperties("food", result);
                logger.info(result);
                return input;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {
                // 在最后一个Pipe中关闭线程池
            }

            @Override
            protected void last() {

            }
        };

        //将第二条管道加入管道线
        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);
        pipeline.addAsThreadPoolBasedPipe(
                new OldOutputAdapt(
                        "", new TextFileTarget("C:\\Users\\joshua\\Desktop\\123.txt", "food")), executorService);
        //管道线初始化
        pipeline.init(pipeline.newDefaultPipelineContext());

        pipeline.process(null);

        //关闭所有管道
        pipeline.shutdown(10, TimeUnit.SECONDS);



    }

}
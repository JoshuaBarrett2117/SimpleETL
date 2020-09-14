package com.code.pipeline.etl.input;

import com.code.pipeline.core.Pipe;
import com.code.pipeline.core.PipeException;
import com.code.pipeline.core.SimplePipeline;
import com.code.pipeline.etl.transformer.AbstractTransformerPipe;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBasedPipeDemo {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolBasedPipeDemo.class);

    @Test
    public void test() {
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
        Pipe<Void, String> inputPipe = new AbstractInputPipe<String>() {
            int count = 10;
            int ii = 0;

            @Override
            public boolean hasNext() {
                return ii++ < count;
            }

            @Override
            public String next() {
                return "Food-" + ii;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

            }
        };
        pipeline.addPipe(inputPipe);
        /*
         * 创建第一条管道
         */
        Pipe<String, String> pipe = new AbstractTransformerPipe<String, String>() {
            @Override
            protected String doProcess(String input) throws PipeException {
                String result = input + "->[pipe1," + Thread.currentThread().getName() + "]";
                logger.info(result);
                return result;
            }
        };
        /*
         * 将第一条管道加入线程池
         */
        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        /*
         * 创建第二条
         */
        pipe = new AbstractTransformerPipe<String, String>() {
            @Override
            protected String doProcess(String input) throws PipeException {
                String result = input + "->[pipe2," + Thread.currentThread().getName() + "]";
                logger.info(result);
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {
                // 在最后一个Pipe中关闭线程池
                logger.debug("最后一个管道关闭时候队列的大小" + executorService.getQueue().size());
                executorService.shutdown();
                try {
                    executorService.awaitTermination(timeout, unit);
                } catch (InterruptedException e) {
                    ;
                }
            }
        };

        //将第二条管道加入管道线
        pipeline.addAsThreadPoolBasedPipe(pipe, executorService);

        //管道线初始化
        pipeline.init(pipeline.newDefaultPipelineContext());
        try {
            pipeline.process(null);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pipeline.shutdown(1, TimeUnit.SECONDS);

    }

}
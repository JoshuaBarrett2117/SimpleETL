package com.code.pipeline.etl.input;

import com.code.pipeline.core.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleWorkerPipeDemo {
    private static final Logger logger = LoggerFactory.getLogger(SingleWorkerPipeDemo.class);

    @Test
    public void test() throws InterruptedException {
        final ThreadPoolExecutor executorSerivce  =
                new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final ThreadPoolExecutor executorSerivce2  =
                new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final ThreadPoolExecutor executorSerivce3  =
                new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final SimplePipeline<String, String> pipeline =
                new SimplePipeline<String, String>("pipeline");
        Pipe<String, String> pipe = new AbstractSingleWorkerPipe<String, String>("pipe1", new IWorker<String, String>() {
            @Override
            public String doRun(String input) throws PipeException {
                String result = input + "->[pipe1," + Thread.currentThread().getName() + "]";
                logger.info(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

            }
        }) {

        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorSerivce);

        pipe = new AbstractSingleWorkerPipe<String, String>("pipe2", new IWorker<String, String>() {
            @Override
            public String doRun(String input) throws PipeException {
                String result = input + "->[pipe2," + Thread.currentThread().getName() + "]";
                logger.info(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

            }
        }) {

        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorSerivce2);

        pipe =new AbstractSingleWorkerPipe<String, String>("pipe3", new IWorker<String, String>() {
            @Override
            public String doRun(String input) throws PipeException {
                String result = input + "->[pipe3," + Thread.currentThread().getName() + "]";
                logger.info(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {

            }
        }) {

        };

        pipeline.addAsThreadPoolBasedPipe(pipe, executorSerivce3);

        pipeline.init(pipeline.newDefaultPipelineContext());

        int N = 10;
        try {
            for (int i = 0; i < N; i++) {
                pipeline.process("Task-" + i);
            }
        } catch (IllegalStateException e) {
            ;
        } catch (InterruptedException e) {
            ;
        }
        pipeline.shutdown(1, TimeUnit.MILLISECONDS);
        logger.info("结束");

    }


}
package com.code.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBasedPipeDemo {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolBasedPipeDemo.class);

    public static void main(String[] args) {
        /*
         * 创建线程池
         */
        final ThreadPoolExecutor executorService =
                new ThreadPoolExecutor(1,
                        Runtime.getRuntime().availableProcessors() * 2,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread t = new Thread(r);
                                t.setDaemon(true);
                                return t;
                            }
                        }, new ThreadPoolExecutor.CallerRunsPolicy());
        /*
         * 创建管道线对象
         */
        final SimplePipeline<String, String> pipeline = new SimplePipeline<String, String>();

        /*
         * 创建第一条管道
         */
        Pipe<String, String> pipe = new AbstractPipe<String, String>() {

            @Override
            protected String doProcess(String input) throws PipeException {
                String result = input + "->[pipe1," + Thread.currentThread().getName() + "]";
                logger.info("处理数据:[{}]", result);
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
        pipe = new AbstractPipe<String, String>() {
            @Override
            protected String doProcess(String input) throws PipeException {
                String result = input + "->[pipe2," + Thread.currentThread().getName() + "]";
                logger.info(result);
                try {
                    Thread.sleep(new Random().nextInt(200));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }

            @Override
            public void shutdown(long timeout, TimeUnit unit) {
                // 在最后一个Pipe中关闭线程池
                //logger.debug("最后一个管道关闭时候队列的大小" + executorService.getQueue().size());
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

        int N = 5;
        try {
            for (int i = 0; i < N; i++) {
                pipeline.process("Task-" + i);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pipeline.shutdown(1, TimeUnit.SECONDS);

    }

}
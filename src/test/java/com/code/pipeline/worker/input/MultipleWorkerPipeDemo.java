package com.code.pipeline.worker.input;

import com.code.pipeline.core.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultipleWorkerPipeDemo {
    private static final Logger logger = LoggerFactory.getLogger(MultipleWorkerPipeDemo.class);

    @Test
    public void test() throws InterruptedException {
        final ThreadPoolExecutor executorSerivce =
                new ThreadPoolExecutor(1, 3,
                        60, TimeUnit.MINUTES,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        final SimplePipeline<String, String> pipeline =
                new SimplePipeline<String, String>("pipeline");
        Pipe<String, String> pipe = new AbstractTransformerMultipleWorkerPipe<String, String>("pipe1",
                newWorker("pipe1-1")
                , newWorker("pipe1-2")
                , newWorker("pipe1-3")
                , newWorker("pipe1-4")
        ) {

        };

        pipeline.addPipe(pipe);

        pipe = new AbstractTransformerMultipleWorkerPipe<String, String>("pipe2",
                newWorker("pipe2-1")
                , newWorker("pipe2-2")
                , newWorker("pipe2-3")
                , newWorker("pipe2-4")
        ) {

        };

        pipeline.addPipe(pipe);

        pipe = new AbstractTransformerMultipleWorkerPipe<String, String>("pipe3",
                newWorker("pipe3-1")
                , newWorker("pipe3-2")
                , newWorker("pipe3-3")
                , newWorker("pipe3-4")
        ) {

        };

        pipeline.addPipe(pipe);

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

    @NotNull
    private AbstractTransformerWorker<String, String> newWorker(String s) {
        return new AbstractTransformerWorker<String, String>(s) {
            @Override
            public String doRun(String input) throws PipeException {
                String result = input + "->[" + s + "," + Thread.currentThread().getName() + "]";
                logger.info(result);
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    ;
                }
                return result;
            }
        };
    }


}
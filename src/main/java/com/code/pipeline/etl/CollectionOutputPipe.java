package com.code.pipeline.etl;

import com.code.pipeline.core.AbstractTransformerMultipleWorkerPipe;
import com.code.pipeline.core.AbstractTransformerWorker;
import com.code.pipeline.core.PipeException;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * @author liufei
 * @Description
 * @Date 2021/1/8 13:50
 */
public class CollectionOutputPipe<IN> extends AbstractTransformerMultipleWorkerPipe<IN, Void> {


    public CollectionOutputPipe(BlockingQueue<IN> workQueue, String name, AbstractTransformerWorker... workers) {
        super(workQueue, name, workers);
    }

    public CollectionOutputPipe(String name, AbstractTransformerWorker<IN, Void>... workers) {
        super(name, workers);
    }

    @Override
    protected void doRun(AbstractTransformerWorker<IN, Void> worker) throws PipeException, InterruptedException {
        super.doRun(worker);
    }
}

//package com.code.pipeline.core;
//
//import java.util.Collection;
//
///**
// * @author liufei
// * @Description
// * @Date 2021/1/8 14:41
// */
//public class CollectionTransFormerPipeInputDecorator<IN, OUT> extends AbstractTransformerMultipleWorkerPipe<Collection<IN>, OUT> {
//    protected AbstractTransformerMultipleWorkerPipe<IN, OUT> pipe;
//
//    public CollectionTransFormerPipeInputDecorator(AbstractTransformerMultipleWorkerPipe<IN, OUT> pipe) {
//        super(pipe.name, pipe.workers);
//    }
//
//    @Override
//    protected void doRun(AbstractTransformerWorker<Collection<IN>, OUT> worker) throws PipeException, InterruptedException {
//        Collection<IN> input = workQueue.take();
//        for (IN input : input) {
//            OUT out = input.worker.doRun(input);
//        }
//
//        if (null != nextPipe) {
//            if (null != out) {
//                ((Pipe<OUT, ?>) nextPipe).process(out);
//            }
//        }
//    }
//}

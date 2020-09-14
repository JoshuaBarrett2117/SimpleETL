package com.code.pipeline.core;

import java.util.Iterator;

/**
 * @param <T>
 * @param <OUT>
 */
public class BatchPipeline<T, OUT> extends SimplePipeline<Iterator<T>, OUT> {
    @Override
    public void process(Iterator<T> input) throws InterruptedException {
        //从队列中取出一个头部的管道
        Pipe<T, ?> firstPipe = (Pipe<T, ?>) pipes.peek();
        while (input.hasNext()) {
            firstPipe.process(input.next());
        }
    }
}
package com.code.pipeline.core;

import java.util.concurrent.TimeUnit;

/**
 * 管道接口，责任链模式，设置下游管道
 *
 * @param <IN>  管道输入参数
 * @param <OUT> 管道输出参数
 */
public interface Pipe<IN, OUT> {
    /**
     * 设置当前Pipe实例的下一个Pipe实例。
     *
     * @param nextPipe 下一个Pipe实例
     */
    void setNextPipe(Pipe<?, ?> nextPipe);

    /**
     * 初始化当前Pipe实例对外提供的服务。
     *
     * @param pipeCtx
     */
    void init(PipeContext pipeCtx);

    /**
     * 停止当前Pipe实例对外提供的服务。
     *
     * @param timeout
     * @param unit
     */
    void shutdown(long timeout, TimeUnit unit);

    /**
     * 对输入元素进行处理，并将处理结果作为下一个Pipe实例的输入。
     */
    void process(IN input) throws InterruptedException;
}
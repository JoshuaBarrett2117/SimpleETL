package com.code.pipeline;

/**
 * 用于组织管道与管道之间的关系
 *
 * @param <IN>
 * @param <OUT>
 */
public interface Pipeline<IN, OUT> extends Pipe<IN, OUT> {

    /**
     * 往该Pipeline实例中添加一个Pipe实例。
     *
     * @param pipe Pipe实例
     */
    void addPipe(Pipe<?, ?> pipe);
}
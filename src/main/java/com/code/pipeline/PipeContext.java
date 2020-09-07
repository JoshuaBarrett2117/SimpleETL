package com.code.pipeline;

/**
 * 管道上下文，贯穿整个管道线
 */
public interface PipeContext {
    /**
     * 用于对处理阶段抛出的异常进行处理.
     *
     * @param exp
     */
    void handleError(PipeException exp);
}
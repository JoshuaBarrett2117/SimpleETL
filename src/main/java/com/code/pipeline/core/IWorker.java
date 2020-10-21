package com.code.pipeline.core;

import java.util.concurrent.TimeUnit;

/**
 * 文件名称: IWorker.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/10/20     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public interface IWorker<IN, OUT> {
    OUT doRun(IN in) throws PipeException;

    void shutdown(long timeout, TimeUnit unit);
}

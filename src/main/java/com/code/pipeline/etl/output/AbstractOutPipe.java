package com.code.pipeline.etl.output;

import com.code.pipeline.core.AbstractPipe;

import java.util.concurrent.TimeUnit;

/**
 * 文件名称: AbstractOutPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/9     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public abstract class AbstractOutPipe<IN> extends AbstractPipe<IN, Void> implements IOutputPipe<IN> {

    @Override
    public void process(IN input) throws InterruptedException {
        out(input);
    }

    @Override
    public abstract void shutdown(long timeout, TimeUnit unit);
}

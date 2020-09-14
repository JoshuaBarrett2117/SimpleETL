package com.code.pipeline.etl.input;

import com.code.pipeline.core.AbstractPipe;
import com.code.pipeline.core.Pipe;

import java.util.concurrent.TimeUnit;

/**
 * 文件名称: IInputPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/9     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public abstract class AbstractInputPipe<OUT> extends AbstractPipe<Void, OUT> implements IInputPipe<OUT> {
    @Override
    public void process(Void input) throws InterruptedException {
        while (this.hasNext()) {
            OUT out = this.next();
            if (null != nextPipe) {
                if (null != out) {
                    ((Pipe<OUT, ?>) nextPipe).process(out);
                }
            }
        }
    }

    @Override
    public abstract void shutdown(long timeout, TimeUnit unit);
}

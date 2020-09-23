package com.code.pipeline.etl.input;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractPipe;
import com.code.pipeline.core.Pipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractInputPipe extends AbstractPipe<Void, DataRowModel> implements IInputPipe {
    private static final Logger logger = LoggerFactory.getLogger(AbstractInputPipe.class);

    @Override
    public void process(Void input) throws InterruptedException {
        while (this.hasNext()) {
            DataRowModel out = this.next();
            if (null != nextPipe) {
                if (null != out) {
                    ((Pipe<DataRowModel, ?>) nextPipe).process(out);
                }
            }
        }
        logger.info("输入插件抽取完成");
        this.over();
    }

    @Override
    protected void last() {

    }

    @Override
    public abstract void shutdown(long timeout, TimeUnit unit);
}

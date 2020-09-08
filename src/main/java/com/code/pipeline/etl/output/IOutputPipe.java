package com.code.pipeline.etl.output;

import com.code.pipeline.core.Pipe;

/**
 * 文件名称: IOutputPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/8     		@author(创建:创建文件)
 * ====================================================
 * 类描述：输入管道
 *
 * @author liufei
 */
public interface IOutputPipe<IN> extends Pipe<IN, Void> {

    /**
     * 输出
     *
     * @return
     */
    void out(IN out);

}

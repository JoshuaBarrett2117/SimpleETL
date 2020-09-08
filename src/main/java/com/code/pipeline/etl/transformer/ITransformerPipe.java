package com.code.pipeline.etl.transformer;

import com.code.pipeline.core.Pipe;

/**
 * 文件名称: ITransformerPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/8     		@author(创建:创建文件)
 * ====================================================
 * 类描述：转换管道
 *
 * @author liufei
 */
public interface ITransformerPipe<IN, OUT> extends Pipe<IN, OUT> {
    /**
     * 转换流程
     *
     * @param in 上游输入数据
     * @return 下游数据
     */
    OUT transformer(IN in);
}

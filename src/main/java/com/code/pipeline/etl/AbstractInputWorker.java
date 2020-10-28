package com.code.pipeline.etl;

import com.code.pipeline.core.AbstractWorker;

/**
 * 文件名称: InputPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/10/28     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public abstract class AbstractInputWorker<OUT> extends AbstractWorker implements IInputWorker<OUT> {

    public AbstractInputWorker(String name) {
        super(name);
    }

}

package com.code.pipeline.etl.output;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.Pipe;

import java.util.List;

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
public interface IOutputPipe extends Pipe<DataRowModel, Void> {

    /**
     * 输出
     *
     * @return
     */
    void out(List<DataRowModel> out);

}

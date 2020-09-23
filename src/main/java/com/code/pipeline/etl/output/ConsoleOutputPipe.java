package com.code.pipeline.etl.output;

import com.alibaba.fastjson.JSONObject;
import com.code.common.dao.core.model.DataRowModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文件名称: FileOutputPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/9/9     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class ConsoleOutputPipe extends AbstractOutPipe {
    public ConsoleOutputPipe(String outputTable) {
        super(outputTable);
    }

    @Override
    public void out(List<DataRowModel> out) {
        for (DataRowModel dataRowModel : out) {
            System.out.println("indexName: " + "【" + JSONObject.toJSONString(dataRowModel) + "】");
        }
    }


    @Override
    public void shutdown(long timeout, TimeUnit unit) {

    }
}

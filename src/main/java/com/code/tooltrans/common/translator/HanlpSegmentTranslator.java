package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IIteratorTranslator;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.Iterator;
import java.util.List;

/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved.
 * ====================================================
 * 文件名称: Assert.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/8/27     		@author(创建:创建文件)
 * ====================================================
 * 类描述：正则匹配的转换器
 *
 * @author liufei
 */
public class HanlpSegmentTranslator implements IIteratorTranslator {

    private String key;
    private Segment segment;

    public HanlpSegmentTranslator(String key, Segment segment) {
        this.key = key;
        this.segment = segment;
    }

    @Override
    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
        return new Iterator<DataRowModel>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public DataRowModel next() {
                DataRowModel next = iterator.next();
                Object o = next.get(key);
                if (o == null) {
                    return null;
                } else {
                    List<Term> seg = segment.seg(o.toString());
                    next.addProperties("seg", seg);
                    return next;
                }
            }
        };
    }
}

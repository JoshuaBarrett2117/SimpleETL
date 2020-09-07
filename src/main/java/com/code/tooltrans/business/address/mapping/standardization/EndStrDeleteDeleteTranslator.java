package com.code.tooltrans.business.address.mapping.standardization;

import com.code.common.dao.core.model.DomainElement;
import com.code.tooltrans.common.IIteratorTranslator;

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
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 删除交叉口后面的文字
 *
 * @author liufei
 */
public class EndStrDeleteDeleteTranslator implements IIteratorTranslator {
    private String key;
    private List<String> endTexts;

    public EndStrDeleteDeleteTranslator(String key, List<String> endTexts) {
        this.key = key;
        this.endTexts = endTexts;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        return new Iterator<DomainElement>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public DomainElement next() {
                DomainElement next = iterator.next();
                String s = next.getProperties().getOrDefault(key, "").toString();
                for (String endText : endTexts) {
                    int indexOf = s.indexOf(endText);
                    if (indexOf != -1) {
                        next.addProperties(key, s.substring(0, indexOf + endText.length()));
                    }
                }
                return next;
            }
        };
    }
}

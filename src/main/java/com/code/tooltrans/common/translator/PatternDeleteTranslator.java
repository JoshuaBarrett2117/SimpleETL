package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DomainElement;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class PatternDeleteTranslator implements IIteratorTranslator {

    private String key;
    private Pattern p;
    private boolean isReplaceAll;

    public PatternDeleteTranslator(String key, Pattern p) {
        this.key = key;
        this.p = p;
        this.isReplaceAll = false;
    }

    public PatternDeleteTranslator(String key, Pattern p, boolean isReplaceAll) {
        this.key = key;
        this.p = p;
        this.isReplaceAll = isReplaceAll;
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
                Object o = next.get(key);
                if (o == null) {
                    return null;
                } else {
                    Matcher matcher = p.matcher(o.toString().trim());
                    if (isReplaceAll) {
                        next.addProperties("text", matcher.replaceAll("").trim());
                    } else {
                        next.addProperties("text", matcher.replaceFirst("").trim());
                    }
                    return next;
                }
            }
        };
    }
}

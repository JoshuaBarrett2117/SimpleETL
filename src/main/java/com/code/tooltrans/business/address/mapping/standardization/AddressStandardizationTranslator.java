package com.code.tooltrans.business.address.mapping.standardization;

import com.code.common.dao.core.model.DomainElement;
import com.code.common.utils.StringUtils;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.Iterator;

/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved.
 * ====================================================
 * 文件名称: Assert.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/8/31     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public class AddressStandardizationTranslator implements IIteratorTranslator {
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
                Object address = next.get("ADDRESS");
                if (address == null
                        || StringUtils.isBlank(address.toString())
                        || address.toString().equals("[]")
                ) {
                    return null;
                }
                Object text = next.get("text");
                if (text == null || StringUtils.isBlank(text.toString())) {
                    return null;
                }
                address = address.toString().replaceAll(",", "，");
                next.addProperties("SRC", "" +
                        next.get("PNAME") + next.get("ADNAME") + address
                );
                next.addProperties("STANDARD", "" +
                        next.get("PNAME") + next.get("ADNAME") + text
                );
                return next;
            }
        };
    }
}

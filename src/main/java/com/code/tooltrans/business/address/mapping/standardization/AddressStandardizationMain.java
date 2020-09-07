package com.code.tooltrans.business.address.mapping.standardization;

import com.code.common.dao.core.model.DomainElement;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.IIteratorTranslator;
import com.code.tooltrans.common.target.text.TextFileTarget;

import java.util.Iterator;
import java.util.Properties;

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
public class AddressStandardizationMain extends AddressStandardizationTrans {

    public static void main(String[] args) {
        new AddressStandardizationMain().deal(null,null);
    }

    @Override
    protected IIteratorTranslator lastStandardizationTrans() {
        return new IIteratorTranslator() {
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
                        next.addProperties("text", "" + next.get("ID") + "," + next.get("SRC") + "," + next.get("STANDARD"));
                        return next;
                    }
                };
            }
        };
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
//        return new ElasticsearchTarget(properties);
//        return new ConsoleTarget();
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\高德天津地理数据标准化.csv", "text");
    }
}

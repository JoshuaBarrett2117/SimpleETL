package business.algorithm.dlwz;

import common.CommonUtil;
import  dao.core.model.DomainElement;
import common.IIteratorTranslator;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 15:21
 */
public class XMDlwzTransIterator implements IIteratorTranslator {

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
                Map<String, Object> properties = next.getProperties();
                return CommonUtil.createDictDoc(properties.get("NAME").toString(), Arrays.asList("I-地理位置-Xiang2Xi4Di4Zhi3"));
            }

        };
    }


}

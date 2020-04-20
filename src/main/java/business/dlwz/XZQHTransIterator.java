package business.dlwz;

import common.CommonUtil;
import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 15:21
 */
public class XZQHTransIterator implements IIteratorTranser {

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
                return CommonUtil.createDictDoc(next.get("MC").toString(),Arrays.asList("I-地理位置-Xiang2Xi4Di4Zhi3"));
            }
        };
    }


}

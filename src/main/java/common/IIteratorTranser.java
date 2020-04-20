package common;

import com.code.common.dao.model.DomainElement;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 15:20
 */
public interface IIteratorTranser {
    Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator);
}

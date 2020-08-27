package common;

import  dao.core.model.DomainElement;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 15:20
 */
public interface IIteratorTranslator {
    Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator);
}

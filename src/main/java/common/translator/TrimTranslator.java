package common.translator;

import  dao.core.model.DomainElement;
import common.IIteratorTranslator;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/10 15:05
 */
public class TrimTranslator implements IIteratorTranslator {
    private String key;

    public TrimTranslator(String key) {
        this.key = key;
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
                next.addProperties(key, next.get(key).toString().replace(" ", ""));
                return next;
            }
        };
    }
}

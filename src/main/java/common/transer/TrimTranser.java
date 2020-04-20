package common.transer;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/10 15:05
 */
public class TrimTranser implements IIteratorTranser {
    private String key;

    public TrimTranser(String key) {
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

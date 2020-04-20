package business.kg;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:32
 */
public class KgToEsTranser implements IIteratorTranser {

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
                String[] texts = next.get("text").toString().split("\\s");
                if (texts.length < 3) {
                    return null;
                }
                next.addProperties("subject", replace(texts[0]));
                next.addProperties("predicate", replace(texts[1]));
                next.addProperties("object", replace(texts[2]));
                next.removeProperties("text");
                return next;
            }
        };
    }

    private String replace(String text) {
        return text.replace(" ", "").replace("<", "").replace(">", "");
    }

}

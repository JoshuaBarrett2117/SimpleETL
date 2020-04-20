package common.transer;

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
public class StringDuplicateRemovalTranser implements IIteratorTranser {

    private String key;

    private Map<String, DomainElement> temp = new ConcurrentHashMap<>();
    private static final DomainElement EMPTY_DE = new DomainElement();
    private AtomicLong count = new AtomicLong();

    public StringDuplicateRemovalTranser(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {

        return new Iterator<DomainElement>() {
            String text;
            boolean isOut = true;

            @Override
            public boolean hasNext() {
                if (!isOut) {
                    return true;
                }
                while (iterator.hasNext()) {
                    DomainElement next = iterator.next();
                    text = next.get(key).toString().trim();
                    if (!temp.containsKey(text)) {
                        next.addProperties(key, text);
                        temp.put(text, next);
                        isOut = false;
                        return true;
                    } else {
                        count.incrementAndGet();
                    }
                }
                text = null;
                temp = null;
                System.out.println(Thread.currentThread().getName() + ":重复了" + count.get() + "个词");
                return false;
            }

            @Override
            public DomainElement next() {
                isOut = true;
                DomainElement domainElement = temp.get(text);
                temp.put(text, EMPTY_DE);
                return domainElement;
            }
        };
    }
}

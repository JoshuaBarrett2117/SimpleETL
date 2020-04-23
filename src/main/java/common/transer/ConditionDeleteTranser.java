package common.transer;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 18:56
 */
public class ConditionDeleteTranser implements IIteratorTranser {
    private Condition condition;
    private String key;

    public ConditionDeleteTranser(String key, Condition condition) {
        this.condition = condition;
        this.key = key;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {

        return new Iterator<DomainElement>() {
            int count = 0;

            @Override
            public boolean hasNext() {
                boolean b = iterator.hasNext();
                if (!b) {
                    System.out.println("条件过滤：删除了" + count + "条数据");
                }
                return b;
            }

            @Override
            public DomainElement next() {
                DomainElement next = iterator.next();
                if (condition.isFilter(next, key)) {
                    count++;
                    return null;
                }
                return next;
            }
        };
    }

    public interface Condition {
        boolean isFilter(DomainElement domainElement, String key);
    }
}

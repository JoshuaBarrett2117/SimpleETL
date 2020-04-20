package common.transer;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 16:14
 */
public class ElementSplitTranser implements IIteratorTranser {

    private Func func;

    public ElementSplitTranser(Func... funcs) {
        this.func = new Func() {
            @Override
            public List<DomainElement> split(DomainElement content) {
                Set<DomainElement> result = new HashSet<>();
                for (Func func1 : funcs) {
                    List<DomainElement> split = func1.split(content);
                    if (split != null) {
                        result.addAll(split);
                    }
                }
                return new ArrayList<>(result);
            }
        };
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        return new Iterator<DomainElement>() {

            Iterator<DomainElement> temps = Collections.EMPTY_LIST.iterator();

            @Override
            public boolean hasNext() {
                //同时没有的时候，结束迭代
                if (!iterator.hasNext() && !temps.hasNext()) {
                    return false;
                }
                return temps.hasNext() || iterator.hasNext();
            }

            @Override
            public DomainElement next() {
                if (!temps.hasNext()) {
                    if (iterator.hasNext()) {
                        DomainElement next = iterator.next();
                        List<DomainElement> words = func.split(next);
                        temps = words.iterator();
                    }
                }
                if (!temps.hasNext()) {
                    return null;
                }
                return temps.next();
            }
        };
    }

    public interface Func {
        List<DomainElement> split(DomainElement content);
    }


}

package common.transer;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 全量数据收集器
 *
 * @author liufei
 * @Description
 * @Date 2020/4/24 11:09
 */
public class AllDataDealTranser implements IIteratorTranser {
    private Func func;

    public AllDataDealTranser(Func func) {
        this.func = func;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        List<DomainElement> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return func.collect(result).iterator();
    }

    public interface Func {
        List<DomainElement> collect(List<DomainElement> list);
    }
}

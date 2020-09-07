package com.code.tooltrans.common.translator;

import com.code.common.dao.core.model.DomainElement;
import com.code.tooltrans.common.IIteratorTranslator;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/8 16:14
 */
public class StringSplitTranslator implements IIteratorTranslator {

    private Func func;
    private String key;

    public StringSplitTranslator(String key, Func... funcs) {
        this.key = key;
        this.func = new Func() {
            @Override
            public List<String> split(String content) {
                Set<String> result = new HashSet<>();
                for (Func func1 : funcs) {
                    result.addAll(func1.split(content));
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
                        List<String> words = func.split(next.get(key).toString());
                        List<DomainElement> elements = new ArrayList<>();
                        for (String word : words) {
                            DomainElement d = new DomainElement();
                            for (Map.Entry<String, Object> entry : next.getProperties().entrySet()) {
                                d.addProperties(entry.getKey(), entry.getValue());
                            }
                            d.addProperties(key, word);
                            elements.add(d);
                        }
                        temps = elements.iterator();
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
        List<String> split(String content);
    }


}

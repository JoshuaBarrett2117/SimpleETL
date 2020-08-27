package business.algorithm.kg;

import  dao.core.model.DomainElement;
import common.IIteratorTranslator;

import java.util.Iterator;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:32
 */
public class KgToEsTranslator implements IIteratorTranslator {

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

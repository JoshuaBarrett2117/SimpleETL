package business.algorithm.sjword;

import  dao.core.model.DomainElement;
import common.IIteratorTranslator;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:02
 */
public class HanCoreDIrcTimeGetterTranslator implements IIteratorTranslator {

    private static final Pattern pattern = Pattern.compile("\\s");

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {

        return new Iterator<DomainElement>() {
            String[] split;

            @Override
            public boolean hasNext() {
                while (iterator.hasNext()) {
                    DomainElement next = iterator.next();
                    String text = next.get("text").toString();
                    split = pattern.split(text);
                    for (String s : split) {
                        if (s.equals("t")) {
                            return true;
                        }
                    }
                }
                split = null;
                return false;
            }

            @Override
            public DomainElement next() {
                DomainElement element = new DomainElement();
                element.addProperties("text", split[0]);
                return element;
            }
        };
    }
}

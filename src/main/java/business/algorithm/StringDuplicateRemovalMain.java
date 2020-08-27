package business.algorithm;

import  dao.core.model.DomainElement;
import common.*;
import common.source.text.TextFileSource;
import common.target.TextFileTarget;
import common.translator.StringDuplicateRemovalTranslator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:07
 */
public class StringDuplicateRemovalMain extends AbstractMain {

    public static void main(String[] args) {
        new StringDuplicateRemovalMain().deal(null, null);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                trim,
                new StringDuplicateRemovalTranslator("text")
        );
    }

    private IIteratorTranslator trim = new IIteratorTranslator() {
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
                    next.addProperties("text", next.get("text").toString().replace(" ", ""));
                    return next;
                }
            };
        }
    };

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource("C:/Users/joshua/Desktop/文本提取/人人关系词.txt");
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:/Users/joshua/Desktop/文本提取/nr、ns之间关系词.txt", "text");
    }
}

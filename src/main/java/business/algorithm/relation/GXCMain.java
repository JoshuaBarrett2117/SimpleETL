package business.algorithm.relation;

import common.translator.StringDuplicateRemovalTranslator;
import  dao.core.model.DomainElement;
import common.*;
import common.source.text.TextFileSource;
import common.target.TextFileTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/9 10:33
 */
public class GXCMain extends AbstractMain {
    public static void main(String[] args) {
        new GXCMain().deal(null, "monistic_core_words_2");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource("C:\\Users\\joshua\\Desktop\\文本提取\\baike_triples.txt");
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
//                new GXCTranser()
                new IIteratorTranslator() {
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
                                String replace = next.get("text").toString().replace(" ", "");
                                String[] split = replace.split("\\s");
                                if (split.length < 3) {
                                    return null;
                                }
                                next.addProperties("text", split[1].replace(":", ""));
                                return next;
                            }
                        };
                    }
                }
                , new StringDuplicateRemovalTranslator("text")
        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\baike_triples[1].txt", "text");
//        return new ElasticsearchTarget(properties);
    }
}

package business.relation;

import common.transer.StringDuplicateRemovalTranser;
import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.FileTarget;

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
    protected IDataSource dataSource(Properties properties) {
        return new FileSource("C:\\Users\\joshua\\Desktop\\文本提取\\baike_triples.txt");
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
//                new GXCTranser()
                new IIteratorTranser() {
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
                , new StringDuplicateRemovalTranser("text")
        );
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new FileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\baike_triples[1].txt", "text");
//        return new ElasticsearchTarget(properties);
    }
}

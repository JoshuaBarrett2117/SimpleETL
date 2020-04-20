package business;

import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.FileTarget;
import common.transer.StringDuplicateRemovalTranser;

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
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                trim,
                new StringDuplicateRemovalTranser("text")
        );
    }

    private IIteratorTranser trim = new IIteratorTranser() {
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
    protected IDataSource getDataSource(Properties properties) {
        return new FileSource("C:/Users/joshua/Desktop/文本提取/人人关系词.txt");
    }

    @Override
    protected IDataTarget getDataTarget(Properties properties) {
        return new FileTarget("C:/Users/joshua/Desktop/文本提取/nr、ns之间关系词.txt", "text");
    }
}

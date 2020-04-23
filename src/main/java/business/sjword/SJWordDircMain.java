package business.sjword;

import common.CommonUtil;
import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.ElasticsearchTarget;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 13:41
 */
public class SJWordDircMain extends AbstractMain {
    public static void main(String[] args) {
        new SJWordDircMain().deal(null,"monistic_core_words_2");
    }
    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(new IIteratorTranser() {
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
                        String replace = next.get("text").toString().trim().replace(" ", "");
                        return CommonUtil.createDictDoc(replace,Arrays.asList("T-时间-模糊时间"));
                    }
                };
            }
        });
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new FileSource("C:\\Users\\joshua\\Desktop\\文本提取\\时间词.txt");
    }


}

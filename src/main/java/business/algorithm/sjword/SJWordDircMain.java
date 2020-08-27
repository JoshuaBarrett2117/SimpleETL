package business.algorithm.sjword;

import common.CommonUtil;
import  dao.core.model.DomainElement;
import common.*;
import common.source.text.TextFileSource;
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
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(new IIteratorTranslator() {
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
    protected IDataTarget buildDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource("C:\\Users\\joshua\\Desktop\\文本提取\\时间词.txt");
    }


}

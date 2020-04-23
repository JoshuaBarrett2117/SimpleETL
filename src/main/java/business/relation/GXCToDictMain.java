package business.relation;

import common.CommonUtil;
import common.transer.StringDuplicateRemovalTranser;
import common.transer.TrimTranser;
import com.code.common.dao.model.DomainElement;
import common.*;
import common.source.FileSource;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/9 10:33
 */
public class GXCToDictMain extends AbstractMain {
    public static void main(String[] args) {
        new GXCToDictMain().deal(null, "monistic_core_words_2");
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new FileSource("C:\\Users\\joshua\\Desktop\\文本提取\\人人关系词去重.txt");
    }

    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new TrimTranser("text")
                , new IIteratorTranser() {
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
                                return CommonUtil.createDictDoc(next.get("text").toString(), Arrays.asList("R-人员-关系"));
                            }
                        };
                    }
                }
                , new StringDuplicateRemovalTranser("word")
        );
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
//        return new FileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\人或地点之间的关系词.txt", "text");
        return new ElasticsearchTarget(properties);
    }
}

package business.algorithm.relation;

import common.CommonUtil;
import common.translator.StringDuplicateRemovalTranslator;
import common.translator.TrimTranslator;
import  dao.core.model.DomainElement;
import common.*;
import common.source.text.TextFileSource;
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
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource("C:\\Users\\joshua\\Desktop\\文本提取\\人人关系词去重.txt");
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new TrimTranslator("text")
                , new IIteratorTranslator() {
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
                , new StringDuplicateRemovalTranslator("word")
        );
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
//        return new FileTarget("C:\\Users\\joshua\\Desktop\\文本提取\\人或地点之间的关系词.txt", "text");
        return new ElasticsearchTarget(properties);
    }
}

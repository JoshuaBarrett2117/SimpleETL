package business.company;

import com.code.common.dao.model.DomainElement;
import com.code.metadata.base.softwaredeployment.Software;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.*;
import common.source.ElasticsearchSource;
import common.target.OracleTarget;

import java.sql.Connection;
import java.util.*;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/24 10:34
 */
public class WordTransitionProbability extends AbstractMain {
    private static final String SOURCE_TABLE = "instance_jin1rong2ling3yu4cao2_zu3zhi1";
    private static final String TARGET_TABLE = "COMPANY_TRANSITION_PROBABILITY";
    private static Segment segment;

    static {
        segment = HanLP.newSegment();
        segment.enableCustomDictionary(false);
    }

    public static void main(String[] args) {
        IDataSource.Exp sourceSql = new IDataSource.Exp("{\n" +
                "  \"size\": 10000,\n" +
                "  \"query\": {\n" +
                "    \"exists\": {\n" +
                "      \"field\": \"Gong1Si1Ming2Cheng1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": \"Gong1Si1Ming2Cheng1\"\n" +
                "}");
        sourceSql.addTableName(SOURCE_TABLE);
        new WordTransitionProbability().deal(sourceSql, TARGET_TABLE);
    }


    @Override
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(
                new IIteratorTranser() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        return new Iterator<DomainElement>() {
                            boolean isOut = true;
                            Transition transition = new Transition();
                            Iterator<String> wordIter;
                            Iterator<Map.Entry<String, Double>> entryIterator;
                            String word;

                            @Override
                            public boolean hasNext() {
                                if (!isOut) {
                                    return true;
                                }
                                //仅第一次进行时执行，为null时收集上游所有数据
                                if (wordIter == null) {
                                    while (iterator.hasNext()) {
                                        DomainElement next = iterator.next();
                                        String text = next.get("Gong1Si1Ming2Cheng1").toString();
                                        List<Term> terms = segment.seg(text);
                                        for (int i = 0; i < terms.size() - 1; i++) {
                                            transition.addGroup(terms.get(i).word, terms.get(i + 1).word);
                                        }
                                    }
                                    wordIter = transition.words.iterator();
                                }
                                if (wordIter.hasNext()) {
                                    //word还有数据时进行转移矩阵收集
                                    if (entryIterator == null || !entryIterator.hasNext()) {
                                        word = wordIter.next();
                                        Map<String, Double> probablity = transition.getProbablity(word);
                                        entryIterator = probablity.entrySet().iterator();
                                    }
                                }
                                boolean b = wordIter.hasNext() || entryIterator.hasNext();
                                isOut = false;
                                return b;
                            }

                            @Override
                            public DomainElement next() {
                                isOut = true;
                                Map.Entry<String, Double> entry = entryIterator.next();
                                String k = entry.getKey();
                                Double b = entry.getValue();
                                if (b == null || b.isInfinite() || b.isNaN()) {
                                    return null;
                                }
                                DomainElement de = new DomainElement();
                                de.addProperties("SRC_WORD", word);
                                de.addProperties("TARGET_WORD", k);
                                de.addProperties("TRANS_PROBABLITY", b);
                                return de;
                            }
                        };
                    }
                }
        );
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new ElasticsearchSource("192.168.125.5", "9400");
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        RdbDataSource rdbDataSource = new RdbDataSource(properties);
        Connection connection = rdbDataSource.getConnection();
        Software software = new Software();
        software.setCode("oracle");
        //输入源
        return new OracleTarget(connection, software);
    }


    static class Transition {
        Map<String, Integer> tempMap = new HashMap<>();
        Set<String> words = new HashSet<>();

        void addGroup(String a, String b) {
            words.add(a);
            words.add(b);
            String key = a + "," + b;
            if (tempMap.containsKey(key)) {
                tempMap.put(key, tempMap.get(key) + 1);
            } else {
                tempMap.put(key, 1);
            }
        }

        Map<String, Double> getProbablity(String src) {
            Map<String, Double> result = new HashMap<>();
            long total = 0;
            for (String word : words) {
                Integer integer = tempMap.get(src + "," + word);
                total += integer == null ? 0 : integer;
            }
            for (String word : words) {
                Integer integer = tempMap.get(src + "," + word);
                result.put(word, ((double) (integer == null ? 0 : integer)) / total);
            }
            return result;
        }

    }
}

package business.v;

import com.code.common.dao.model.DomainElement;
import common.IIteratorTranser;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/7 14:50
 */
public class VGetTranser implements IIteratorTranser {

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        return new Iterator<DomainElement>() {
            Iterator<DomainElement> resultIter;

            @Override
            public boolean hasNext() {
                if (!iterator.hasNext()) {
                    return false;
                } else {
                    if (resultIter != null && resultIter.hasNext()) {
                        return true;
                    } else {
                        while (iterator.hasNext()) {
                            DomainElement next = iterator.next();
                            List<DomainElement> temp = parseV(next);
                            resultIter = temp.iterator();
                            if (resultIter.hasNext()) {
                                return true;
                            }
                            //两个迭代器都没东西，返回false，循环结束了
                            if (!resultIter.hasNext() && !iterator.hasNext()) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }

            @Override
            public DomainElement next() {
                return resultIter.next();
            }

        };
    }

    /**
     * 找到next中的所有动词
     *
     * @param next 一篇es文档
     * @return
     */
    private List<DomainElement> parseV(DomainElement next) {
        List<DomainElement> temp = new ArrayList<>();
        List<String> segNatureSeq = (List<String>) next.get("seg_nature_seq");
        List<String> labels = Arrays.asList(next.get("label").toString().split("\\s"));
        List<String> segWordSeq = (List<String>) next.get("seg_word_seq");
        List<Integer> segSeqVIndices = getVIndices(segNatureSeq, labels, "NER-C");
        List<String> tagSeq = (List<String>) next.get("tag_seq");
        List<String> tagWordSeq = (List<String>) next.get("tag_word_seq");
        List<Integer> tagSeqVIndices = getVIndices(tagSeq, tagWordSeq, "I-组织-Gong1Si1Ming2Cheng1");
        //无所谓重复，后续会做去重操作
        findVWord(next, temp, segWordSeq, segSeqVIndices);
        findVWord(next, temp, tagWordSeq, tagSeqVIndices);
        return temp;
    }

    private void findVWord(DomainElement next, List<DomainElement> temp, List<String> segWordSeq, List<Integer> segSeqVIndices) {
        for (Integer integer : segSeqVIndices) {
            String word = null;
            word = segWordSeq.get(integer);
            if (isVWord(word)) {
                DomainElement de = new DomainElement();
                de.addProperties("text", word);
                de.addProperties("output", word + "," + next.getId());
                temp.add(de);
            }
        }
    }


    /**
     * 是否是动词
     *
     * @param segNatureSeq   词性列表
     * @param labels         ner标记
     * @param nerLabelPrefix ner标记前缀
     * @return
     */
    private List<Integer> getVIndices(List<String> segNatureSeq, List<String> labels, String nerLabelPrefix) {
        List<Integer> result = new ArrayList<>();
        IndicesRange indicesRange = findNerIndicesRange(labels, nerLabelPrefix);
        if (indicesRange.start == -1) {
            return result;
        }
        for (int i = indicesRange.start; i < indicesRange.end; i++) {
            String tag = segNatureSeq.get(i);
            if (isVTag(tag)) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * @param labels
     * @param nerLabelPrefix
     * @return
     */
    private IndicesRange findNerIndicesRange(List<String> labels, String nerLabelPrefix) {
        IndicesRange indicesRange = new IndicesRange();
        indicesRange.start = -1;
        indicesRange.end = labels.size();
        int temp = indicesRange.end;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).startsWith(nerLabelPrefix)) {
                if (indicesRange.start == -1) {
                    indicesRange.start = i;
                }
                temp = i;
            }
        }
        indicesRange.end = temp;
        return indicesRange;
    }

    private boolean isVTag(String tag) {
        return tag.equals("v");
    }

    private boolean isVWord(String word) {
        return word.length() > 1;
    }

    class IndicesRange {
        int start;
        int end;
    }
}

package business.algorithm.dlwz;

import  dao.core.model.DomainElement;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.IIteratorTranslator;
import common.translator.ElementSplitTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/20 10:31
 */
public class DlwzHanLpTranslator implements IIteratorTranslator {
    private static Segment segment;

    static {
        segment = HanLP.newSegment();
        segment.enableCustomDictionary(true);
    }

    private String key;

    public DlwzHanLpTranslator(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        ElementSplitTranslator splitTranser = new ElementSplitTranslator((content) -> {
            String sentense = content.get(key).toString();
            List<Term> seg = segment.seg(sentense);
            return creatDocs(sentense, seg);
        });
        return splitTranser.transIterator(iterator);
    }

    private List<DomainElement> creatDocs(String sentense, List<Term> seg) {
        List<DomainElement> result = new ArrayList<>();
        for (int i = 0; i < seg.size(); i++) {
            Term term = seg.get(i);
            DomainElement de = new DomainElement();
            de.addProperties("SENTENSE", sentense);
            de.addProperties("CUT_WORD", term.word);
            de.addProperties("WORD_POS", term.nature.toString());
            de.addProperties("INDEX_NUM", i);
            result.add(de);
        }
        return result;
    }
}

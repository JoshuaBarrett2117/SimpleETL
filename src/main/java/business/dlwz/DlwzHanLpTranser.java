package business.dlwz;

import com.code.common.dao.model.DomainElement;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.IIteratorTranser;
import common.transer.ElementSplitTranser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/20 10:31
 */
public class DlwzHanLpTranser implements IIteratorTranser {
    private static final Segment segment = HanLP.newSegment();
    private String key;

    public DlwzHanLpTranser(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        ElementSplitTranser splitTranser = new ElementSplitTranser((content) -> {
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

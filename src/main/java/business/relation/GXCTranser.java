package business.relation;

import com.code.common.dao.model.DomainElement;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import common.IIteratorTranser;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/9 14:16
 */
public class GXCTranser implements IIteratorTranser {

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
                String[] texts = next.get("text").toString().trim().split(",");
                if (texts.length != 3) {
                    return null;
                }
                Segment segment = HanLP.newSegment()
                        .enableNameRecognize(true)
                        .enablePlaceRecognize(true)
                        .enableOrganizationRecognize(true);
                List<Term> seg = segment.seg(texts[0]);
                List<Term> seg2 = segment.seg(texts[2]);
                if (
                        seg.size() == 1
                                && seg2.size() == 1
                                && isNer(seg)
                                && isNer(seg2)
                ) {
                    next.addProperties("text", texts[1]);
                } else {
                    return null;
                }
//                DomainElement doc = CommonUtil.createDictDoc(
//                        texts[0], Arrays.asList("R-人员-关系"), Arrays.asList(Integer.valueOf(texts[1])));
                return next;
            }

            private boolean isNer(List<Term> seg) {
                return seg.get(0).nature.startsWith("nr") || seg.get(0).nature.startsWith("ns");
            }
        };
    }
}

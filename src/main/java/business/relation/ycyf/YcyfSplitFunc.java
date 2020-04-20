package business.relation.ycyf;

import common.transer.StringSplitTranser;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/14 9:23
 */
public class YcyfSplitFunc implements StringSplitTranser.Func {

    private static final String ATT = "定中关系";
    private static final String QUN = "数量关系";
    private static final String COO = "并列关系";
    private static final String APP = "同位关系";
    private static final String ADJ = "附加关系";
    private static final String VOB = "动宾关系";
    private static final String POB = "介宾关系";
    private static final String SBV = "主谓关系";
    private static final String SIM = "比拟关系";
    private static final String TMP = "时间关系";
    private static final String LOC = "处所关系";
    private static final String DE = "的";
    private static final String DI = "地";
    private static final String DEI = "得";
    private static final String SUO = "所";
    private static final String BA = "把";
    private static final String BEI = "被";
    private static final String ADV = "状中结构";
    private static final String CMP = "动补结构";
    private static final String DBL = "兼语结构";
    private static final String CNJ = "关联词";
    private static final String CS = "关联结构";

    private static final String MT = "语态结构";
    private static final String VV = "连谓结构";
    private static final String HED = "核心";
    private static final String FOB = "前置宾语";
    private static final String DOB = "双宾语";
    private static final String TOP = "主题";
    private static final String IS = "独立结构";
    private static final String IC = "独立分句";
    private static final String DC = "依存分句";
    private static final String VNV = "叠词关系";
    private static final String YGC = "一个词";
    private static final String WP = "标点";

    @Override
    public List<String> split(String content) {
        CoNLLSentence coNLLWords = HanLP.parseDependency(content);
        List<String> deal = deal(coNLLWords);
        deal.add(0, "原句：" + content);
        return deal;
    }

    public static void main(String[] args) {
        List<String> strings = new YcyfSplitFunc().deal(HanLP.parseDependency("王五和李四等人国籍都是中国"));
        System.out.println(strings);
    }

    private List<String> deal(CoNLLSentence sentence) {
        List<String> result = new ArrayList<>();
        List<CoNLLWord> ners = new ArrayList<>();
        for (CoNLLWord word : sentence) {
//            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
            if (isNer(word)) {
                ners.add(word);
            }
        }
        List<CoNLLWord[]> nerTrip = new ArrayList<>();
        for (int i = 0; i < ners.size(); i++) {
            for (int j = i + 1; j < ners.size(); j++) {
                CoNLLWord[] temp = new CoNLLWord[2];
                temp[0] = ners.get(i);
                temp[1] = ners.get(j);
                nerTrip.add(temp);
            }
        }
        System.out.println("==========三元组==========");
        for (CoNLLWord[] temp : nerTrip) {
            CoNLLWord ej = temp[1];
            CoNLLWord ei = temp[0];
            CoNLLWord ej_ = findCooOrAtt(ej);
            CoNLLWord ei_ = findCooOrAtt(ei);
            if (ej_ == null || ei_ == null || ej_.equals(ei_)) {
                continue;
            }
            // ei  ej
            //<陈凡,厦门>
            //ei_, ej_
            //黄义炽 厦门
            CoNLLWord vj = findFirsVerd(ej_);
            //去
            CoNLLWord vi = findFirsVerdSbvFob(ei_);
            //去
            if (vj == null || vi == null) {
                continue;
            }
            if (isEqualOrCoo(vi, vj)) {
                System.out.println("<" + ei.LEMMA + ":" + vi.LEMMA + ":" + ej.LEMMA + ">");
            }
            result.add("<" + ei.LEMMA + ":" + vi.LEMMA + ":" + ej.LEMMA + ">");
        }
        return result;
    }

    private boolean isNer(CoNLLWord word) {
        return word.POSTAG.startsWith("nr") || word.POSTAG.startsWith("ns");
    }

    private boolean isEqualOrCoo(CoNLLWord vi, CoNLLWord vj) {
        return vi.equals(vj) || (vj.HEAD.equals(vi) && COO.equals(vj.DEPREL));
    }

    private CoNLLWord findFirsVerd(CoNLLWord e) {
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = e;
        while ((head = head.HEAD) != null) {
            if (head == CoNLLWord.ROOT) {
                break;
            } else {
                if (head.POSTAG.startsWith("v")) {
                    return head;
                }
            }
        }
        return null;
    }

    private CoNLLWord findFirsVerdSbvFob(CoNLLWord e) {
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = e;
        while ((head = head.HEAD) != null) {
            if (head == CoNLLWord.ROOT) {
                break;
            } else {
                if (head.POSTAG.startsWith("v") && (SBV.equals(e.DEPREL) || FOB.equals(e.DEPREL))) {
                    return head;
                }
            }
        }
        return null;
    }

    private CoNLLWord findCooOrAtt(CoNLLWord ej) {
        CoNLLWord p;
        if ((ej.DEPREL.equals(COO) || ej.DEPREL.equals(ATT)) && isNer(ej)) {
            p = ej.HEAD;
            while ((p.DEPREL.equals(COO) || p.DEPREL.equals(ATT)) && isNer(p)) {
                p = p.HEAD;
            }
        } else {
            return ej;
        }
        return p;
    }


}

package business.dlwz.graph;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.MD5;
import com.code.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/23 10:02
 */
public abstract class AbstractPatternChain {
    /**
     * 下一条链
     */
    protected AbstractPatternChain nextChain;

    /**
     * 当前要匹配的正则
     */
    protected Pattern pattern;

    /**
     * 要解析成的type
     */
    protected String type;

    public AbstractPatternChain(Pattern pattern, String type) {
        this.pattern = pattern;
        this.type = type;
    }

    /**
     * @param pId  父亲id
     * @param text 要处理的字符串，为空串时不做任何处理，不丢给下个链
     * @return 解析的图
     */
    public final Graph deal(String pId, String text, String lastText) {
        AbstractPatternChain.Graph graph = new AbstractPatternChain.Graph();
        return this.deal(pId, text, graph, lastText);
    }

    /**
     * @param pId   父亲id
     * @param text  要处理的字符串，为空串时不做任何处理，不丢给下个链
     * @param graph 图
     * @return 解析的图
     */
    private final Graph deal(String pId, String text, Graph graph, String lastText) {
        if (StringUtils.isBlank(text)) {
            if (StringUtils.isBlank(lastText)) {
                return graph;
            }
            text = lastText;
            lastText = null;
        }
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            SplitWord splitWord = matchSplitWord(matcher);
            if (StringUtils.isNotBlank(splitWord.parentn)) {
                //创建当前id
                String thisId = IdUtil.calcVId(pId,splitWord.parentn);
                parseGraph(graph, thisId, pId, splitWord.parentn, type);
                return getGraph(thisId, splitWord.son, graph, lastText);
            }
        }
        //当前没有匹配到就丢到下一级去匹配
        return getGraph(pId, text, graph, lastText);
    }

    private Graph getGraph(String id, String son, Graph graph, String lastText) {
        return nextChain == null ? graph : nextChain.deal(id, son, graph, lastText);
    }


    /**
     * 匹配出下一个链要处理的串
     *
     * @param matcher 匹配器
     * @return
     */
    protected abstract SplitWord matchSplitWord(Matcher matcher);

    private void parseGraph(AbstractPatternChain.Graph graph, String thisId, String pId, String name, String type) {
        DomainElement roadV = new DomainElement();
        roadV.setId(thisId);
        roadV.addProperties("name", name);
        roadV.addProperties("type", type);
        graph.addV(roadV);
        DomainElement e = new DomainElement();
        e.addProperties("in_id", pId);
        e.addProperties("out_id", thisId);
        e.addProperties("type", "dlwz-属于");
        e.setId(IdUtil.calcEdgeId(e));
        graph.addE(e);
    }

    public AbstractPatternChain getNextChain() {
        return nextChain;
    }

    public void setNextChain(AbstractPatternChain nextChain) {
        this.nextChain = nextChain;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Graph {
        List<DomainElement> v = new ArrayList<>();
        List<DomainElement> e = new ArrayList<>();

        void addV(DomainElement d) {
            v.add(d);
        }

        void addE(DomainElement d) {
            e.add(d);
        }
    }

    protected static class SplitWord {
        String son;
        String parentn;
    }


    public static void main(String[] args) {

    }

}

package business.dlwz.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 移除正则匹配的链
 *
 * @author liufei
 * @Description
 * @Date 2020/4/23 10:24
 */
public class RemovePatternChain extends AbstractPatternChain {
    public RemovePatternChain(Pattern pattern, String type) {
        super(pattern, type);
    }


    @Override
    protected SplitWord matchSplitWord(Matcher matcher) {
        SplitWord word = new SplitWord();
        word.parentn = matcher.group();
        word.son = matcher.replaceAll("");
        return word;
    }
}

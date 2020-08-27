package business.algorithm.dlwz.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/23 10:29
 */
public class NoPatternChain extends AbstractPatternChain {

    private static final Pattern p = Pattern.compile(".+");

    public NoPatternChain(String type) {
        super(p, type);
    }

    @Override
    protected SplitWord matchSplitWord(Matcher matcher) {
        SplitWord word = new SplitWord();
        word.parentn = matcher.group();
        word.son = matcher.replaceAll("");
        return word;
    }
}
package business.algorithm.dlwz;

import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/21 13:26
 */
public class StaticPattern {
    /**
     * 匹配数值和之后的数据
     */
    public static final Pattern numberMatchPattern = Pattern.compile("[a-zA-Z]{0,}[0-9]+(-){0,}[0-9]{0,}.+");

    /**
     * 匹配到之后清空匹配到的内容
     */
    public static final Pattern[] matchPs = new Pattern[]{
            //去出XXX号 XXX-XXX等数据
            numberMatchPattern
            , Pattern.compile("(等|附近|东|西|南|北|东南|西南|东北|西北|之|交叉口)(?!路).{0,}$")
    };
    /**
     * 提取“与”,“、”隔开的数据，生成新词，1个变成多个
     */
    public static final Pattern[] splitPs = new Pattern[]{
            Pattern.compile("与|、")
    };
}

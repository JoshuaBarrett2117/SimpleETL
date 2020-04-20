package hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/16 11:18
 */
public class HanlpTest {
    public static void main(String[] args) {
        Segment segment = HanLP.newSegment();
        List<Term> test = segment.seg("小米和小红去了超市");
        System.out.println(test);
    }
}

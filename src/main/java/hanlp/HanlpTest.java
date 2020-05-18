package hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/16 11:18
 */
public class HanlpTest {
    public static void main(String[] args) {

        CoNLLSentence coNLLWords = HanLP.parseDependency("原定2020年现改为2020年，在宜友海鲜大排档（中山路、中山路和中山路口之间）");
        System.out.println(coNLLWords);
//        segment.enableCustomDictionary(true);
//        test = segment.seg("巨龙信息科技有限公司");
//        System.out.println(test);
//        test = segment.seg("网宿科技有限公司");
//        System.out.println(test);


    }
}

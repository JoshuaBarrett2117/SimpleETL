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
        segment.enableCustomDictionary(true);
        segment.enablePlaceRecognize(true);
        segment.enableOrganizationRecognize(true);
        List<Term> test = segment.seg("福建省厦门市集美区杏林街道杏林村鸡儿路310号巨龙信息科技有限公司");
        System.out.println(test);
        segment.enableCustomDictionary(true);
        test = segment.seg("巨龙信息科技有限公司");
        System.out.println(test);
        test = segment.seg("网宿科技有限公司");
        System.out.println(test);


    }
}

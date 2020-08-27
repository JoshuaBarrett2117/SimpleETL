package common;

import  dao.core.model.DomainElement;

import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/9 14:17
 */
public class CommonUtil {
    /**
     * 时间戳归一化到天级别
     *
     * @param a
     * @return
     */
    public static Long getTodayZero(Long a) {
        Date date = new Date();
        date.setTime(a);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long todayZero = calendar.getTimeInMillis();
        return todayZero;
    }

    public static DomainElement createDictDoc(String word, List<String> tagList) {
        return createDictDoc(word, tagList, null);
    }

    public static DomainElement createDictDoc(String word, List<String> tagList, List<Integer> fres) {
        if (fres == null) {
            fres = new ArrayList<>();
            for (String s : tagList) {
                fres.add(360);
            }
        }
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("word", word);
        doc1.put("tag_seq", tagList);
        doc1.put("frequency_seq", fres);
        doc1.put("type", "new_word");
        doc1.put("domain", Arrays.asList(
                "jr",
                "ga",
                "36483b9f02c144ecacbaf0465388d640"));
        long value = System.currentTimeMillis();
        doc1.put("insertTime", value);
        doc1.put("insertDay", CommonUtil.getTodayZero(value));
        DomainElement domainElement = new DomainElement();
        domainElement.setProperties(doc1);
        return domainElement;
    }
}

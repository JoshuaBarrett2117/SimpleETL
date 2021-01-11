package com.code.pipeline.worker.renyuan.worker;

import com.code.common.dao.core.model.DataRowModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class RelationStandarder {
    private static final List<String> qinZiList;
    private static final List<String> peiOuList;
    private static final List<String> qiTaQinShuList;
    private static final String QIN_ZI = "亲子";
    private static final String PEI_OU = "配偶";
    private static final String QI_TA = "其他";

    static {
        qinZiList = new ArrayList<>();
        qinZiList.add("父子");
        qinZiList.add("母子");
        qinZiList.add("父亲");
        qinZiList.add("母亲");
        qinZiList.add("儿子");
        qinZiList.add("女儿");
        peiOuList = new ArrayList<>();
        peiOuList.add("妻子");
        peiOuList.add("丈夫");
        qiTaQinShuList = new ArrayList<>();
        qiTaQinShuList.add("叔");
        qiTaQinShuList.add("伯");
        qiTaQinShuList.add("姨");
        qiTaQinShuList.add("姑");
        qiTaQinShuList.add("侄子");
        qiTaQinShuList.add("侄女");
        qiTaQinShuList.add("堂兄");
        qiTaQinShuList.add("堂弟");
        qiTaQinShuList.add("哥哥");
        qiTaQinShuList.add("弟弟");
    }


    public static void standard(DataRowModel input, String relationTypeField) {
        String relationType = input.getAsString(relationTypeField);
        if (qinZiList.contains(relationType)) {
            input.addProperties(relationType, QIN_ZI);
        } else if (peiOuList.contains(relationType)) {
            input.addProperties(relationType, PEI_OU);
        } else if (qiTaQinShuList.contains(relationType)) {
            input.addProperties(relationType, QI_TA);
        }
    }
}

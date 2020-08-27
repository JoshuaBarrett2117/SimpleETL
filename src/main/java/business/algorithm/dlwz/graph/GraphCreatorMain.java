package business.algorithm.dlwz.graph;

import business.algorithm.dlwz.FJSDLWZDataSourceMain;
import business.algorithm.dlwz.StaticPattern;
import  dao.core.model.DomainElement;
import dao.jdbc.operator.Software;
import common.IDataSource;
import common.IDataTarget;
import common.RdbDataSource;
import common.source.rdb.RdbSource;
import common.target.OracleTarget;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/21 9:58
 */
public class GraphCreatorMain {
    private static final String E_TABLE_NAME = "DLWZ_ROAD_E_2";
    private static final String V_TABLE_NAME = "DLWZ_ROAD_V_2";

    private RdbDataSource rdbDataSource;
    private IDataSource oracleDataSource;
    private IDataTarget oracleTarget;


    /**
     * 通过sql
     * 基于第一次图统计的结果
     * select cut_word,count(1) as count from DLWZ_FC_TEST where dbms_lob.substr(sentense,4000,1) in (select name from DLWZ_ROAD_V where type = '位置'
     * )GROUP BY cut_word ORDER BY count desc;
     * 得到的结果
     */
    /**
     * 行政街道规划
     */
    private static final Pattern streetPattern = Pattern.compile("(.)+街道");
    private static final Pattern roadPattern = Pattern.compile("(.)+(辅路|路|大道)");
    /**
     * 商业街
     */
    private static final Pattern commercialStreePattern = Pattern.compile("(.)+(街|大街|市场)");
    /**
     * 小区
     */
    private static final Pattern residentialPattern = Pattern.compile("(.)+(村|里|小区|花园|新村|家园)");
    /**
     * 商业工业区划
     */
    private static final Pattern businessPattern = Pattern.compile("(.)+(工业区|开发区|大厦|中心|广场)");

    public static void main(String[] args) {
        GraphCreatorMain graphCreatorMain = new GraphCreatorMain();
        try {
            graphCreatorMain.deal();
        } finally {
            graphCreatorMain.rdbDataSource.close(graphCreatorMain.rdbDataSource.getConnection(), null, null);
        }
//        AbstractPatternChain.Graph graph = parseGraph("莲前西路29-101号明发商业广场1层", "pId", "小吃街");
//        System.out.println(1);
//
    }

    private void deal() {
        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Software software = getSoftware();
        oracleDataSource = new RdbSource(getConnection(properties), software);
        oracleTarget = new OracleTarget(getConnection(properties), software);
//        createXzqhGraph();
//        System.out.println("行政区划图构建完毕");
        createFjDlwzGraph();
        System.out.println("地理位置图构建完毕");
    }

    private void createFjDlwzGraph() {
        FJSDLWZDataSourceMain fjsdlwzDataSourceMain = new FJSDLWZDataSourceMain();
        Iterator<DomainElement> iterator = fjsdlwzDataSourceMain.warpTranslator(
                new IDataSource.Exp("select  DISTINCT ADDRESS AS NAME, A.ADCODE,A.NAME AS LAST_SON_NAME from PY_AMAP_LBS_INFO A where ADDRESS !='[]' ")
        );
        createGraph(iterator, (next) -> createFjDlwzSonGraph(next));
    }

    private AbstractPatternChain.Graph createFjDlwzSonGraph(DomainElement next) {
        String text = next.get("NAME").toString();
        String lastSonName = next.get("LAST_SON_NAME").toString();
        String pId = next.get("ADCODE").toString();
        Pattern[] splitPs = StaticPattern.splitPs;
        for (Pattern splitP : splitPs) {
            Matcher matcher = splitP.matcher(text);
            if (matcher.find()) {
                return null;
            }
        }
        AbstractPatternChain.Graph graph = parseGraph(text, pId, lastSonName);
        return graph;
    }

    private static AbstractPatternChain.Graph parseGraph(String text, String pId, String lastSonName) {
        AbstractPatternChain streetPatternChain = new RetainPatternChain(streetPattern, "行政街道规划");
        AbstractPatternChain roadPatternChain = new RetainPatternChain(roadPattern, "路");
        streetPatternChain.setNextChain(roadPatternChain);
        AbstractPatternChain commercialStreePatternChain = new RetainPatternChain(commercialStreePattern, "商业街");
        roadPatternChain.setNextChain(commercialStreePatternChain);
        AbstractPatternChain residentialPatternChain = new RetainPatternChain(residentialPattern, "住宅区");
        commercialStreePatternChain.setNextChain(residentialPatternChain);
        AbstractPatternChain businessPatternChain = new RetainPatternChain(businessPattern, "商业工业区划");
        residentialPatternChain.setNextChain(businessPatternChain);
        AbstractPatternChain previousPatternChain = businessPatternChain;
        for (Pattern matchP : StaticPattern.matchPs) {
            RemovePatternChain currChain = new RemovePatternChain(matchP, "详细地址");
            previousPatternChain.setNextChain(currChain);
            previousPatternChain = currChain;
        }
        AbstractPatternChain last = new NoPatternChain("具体地点");
        previousPatternChain.setNextChain(last);
        return streetPatternChain.deal(pId, text, lastSonName);
    }


    private void createXzqhGraph() {
        createGraph("SELECT DM,MC,XZQH_LEVEL FROM BM_STATS2018_QHYCXDM order by XZQH_LEVEL asc", (next) -> createXzqhSonGraph(next));
    }

    private AbstractPatternChain.Graph createXzqhSonGraph(DomainElement next) {
        AbstractPatternChain.Graph graph = new AbstractPatternChain.Graph();
        String dm = next.get("DM").toString();
        String mc = next.get("MC").toString();
        int level = Integer.valueOf(next.get("XZQH_LEVEL").toString());
        //创建点
        DomainElement tempV = new DomainElement();
        tempV.setId(dm);
        tempV.addProperties("name", mc);
        tempV.addProperties("type", "行政区划" + level);
        graph.addV(tempV);
        //根据level创建边
        if (level >= 1 && level <= 3) {
            if (level != 1) {
                DomainElement tempE = new DomainElement();
                tempE.addProperties("in_id", dm.substring(0, (level - 1) * 2) + createZero(6 - (level - 1) * 2));
                tempE.addProperties("out_id", dm);
                tempE.addProperties("type", "xzqh-属于");
                tempE.setId(IdUtil.calcEdgeId(tempE));
                graph.addE(tempE);
            }
        } else if (level == 4) {
            DomainElement tempE = new DomainElement();
            tempE.addProperties("in_id", dm.substring(0, 6));
            tempE.addProperties("out_id", dm);
            tempE.addProperties("type", "xzqh-属于");
            tempE.setId(IdUtil.calcEdgeId(tempE));
            graph.addE(tempE);
        } else {
            DomainElement tempE = new DomainElement();
            tempE.addProperties("in_id", dm.substring(0, 9) + createZero(3));
            tempE.addProperties("out_id", dm);
            tempE.addProperties("type", "xzqh-属于");
            tempE.setId(IdUtil.calcEdgeId(tempE));
            graph.addE(tempE);
        }
        return graph;
    }

    private String createZero(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append(0);
        }
        return sb.toString();
    }

    private void createGraph(String sql, Function<DomainElement, AbstractPatternChain.Graph> function) {
        Iterator<DomainElement> iterator = oracleDataSource.iterator(new IDataSource.Exp(sql));
        createGraph(iterator, function);
    }

    private void createGraph(Iterator<DomainElement> iterator, Function<DomainElement, AbstractPatternChain.Graph> function) {
        List<DomainElement> vList = new ArrayList<>();
        List<DomainElement> eList = new ArrayList<>();
        long vCount = 0;
        long eCount = 0;
        long start = System.currentTimeMillis();
        while (iterator.hasNext()) {
            DomainElement next = iterator.next();
            AbstractPatternChain.Graph xzqhSonGraph = function.apply(next);
            if (xzqhSonGraph == null) {
                continue;
            }
            if (xzqhSonGraph.e != null) {
                eList.addAll(xzqhSonGraph.e);
            }
            if (xzqhSonGraph.v != null) {
                vList.addAll(xzqhSonGraph.v);
            }
            if (vList.size() > 5000 || eList.size() > 5000) {
                oracleTarget.saveOrUpdate(vList, V_TABLE_NAME);
                vCount += vList.size();
                System.out.println("新增[" + vList.size() + "]个点，共计:" + vCount);
                oracleTarget.saveOrUpdate(eList, E_TABLE_NAME);
                eCount += eList.size();
                System.out.println("新增[" + eList.size() + "]条边，共计:" + eCount);
                System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
                start = System.currentTimeMillis();
                vList = new ArrayList<>();
                eList = new ArrayList<>();
            }
        }
        oracleTarget.saveOrUpdate(vList, V_TABLE_NAME);
        vCount += vList.size();
        System.out.println("新增[" + vList.size() + "]个点，共计:" + vCount);
        oracleTarget.saveOrUpdate(eList, E_TABLE_NAME);
        eCount += eList.size();
        System.out.println("新增[" + eList.size() + "]条边，共计:" + eCount);
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    private Software getSoftware() {
        Software software = new Software();
        software.setCode("oracle");
        return software;
    }

    private Connection getConnection(Properties properties) {
        if (rdbDataSource == null) {
            rdbDataSource = new RdbDataSource(properties);
        }
        return rdbDataSource.getConnection();
    }


}

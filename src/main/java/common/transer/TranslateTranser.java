package common.transer;

import com.alibaba.fastjson.JSONObject;
import com.code.common.dao.model.DomainElement;
import com.code.common.utils.MD5;
import common.IIteratorTranser;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/10 15:24
 */
public class TranslateTranser implements IIteratorTranser {
    private String key;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private static final Random rand = new Random(System.currentTimeMillis());

    private static final String apiUrl = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static final String appId = "20200410000415799";
    private static final String userKey = "F3zjpsJ_UsjekqrM5Ohq";

    public TranslateTranser(String key) {
        this.key = key;
    }

    @Override
    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
        return new Iterator<DomainElement>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public DomainElement next() {
                DomainElement next = iterator.next();
                next.addProperties(key, translate(next.get(key).toString()));
                return next;
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        String q = "charactersInNovel";
        String translate = new TranslateTranser("k").translateWhile(q);
        System.out.println(q + ":" + translate);
        Thread.sleep(1000);
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }

    private String translateWhile(String q) {
        int count = 0;
        while (true && count++ < 3) {
            String translate = translate(q);
            if (translate == null) {
                System.out.println("重试第" + count + "次");
                continue;
            } else {
                return translate;
            }
        }
        return q;
    }

    private String translate(String q) {
        String url = TranslateTranser.apiUrl;
        StringBuilder sb = new StringBuilder();
        sb.append("?q=" + q);
        sb.append("&from=en");
        sb.append("&to=zh");
        sb.append("&appid=" + appId);
        String salt = String.valueOf(rand.nextInt(Integer.MAX_VALUE));
        String sign = MD5.md5(appId + q + salt + userKey);
        sb.append("&salt=" + salt);
        sb.append("&sign=" + sign);
        url += sb.toString();
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        final Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String v = q;
        try {
            String string = response.body().string();
            Map<String, Object> map = JSONObject.parseObject(string, Map.class);
            List<Map<String, String>> transResults = (List<Map<String, String>>) map.get("trans_result");
            StringBuilder sb2 = new StringBuilder();
            if (transResults == null) {
                return null;
            }
            for (Map<String, String> result : transResults) {
                sb2.append(result.get("dst"));
            }
            v = sb2.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response == null ? null : v;
    }


}

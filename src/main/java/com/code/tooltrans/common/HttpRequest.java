package com.code.tooltrans.common;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * HTTP工具
 *
 * @author robinzhang
 */
public class HttpRequest {
    /**
     * 请求类型： GET
     */
    public final static String GET = "GET";
    /**
     * 请求类型： POST
     */
    public final static String POST = "POST";

    private CloseableHttpClient client;
    private RequestConfig requestConfig;
    private CookieStore cookieStore;


    public HttpRequest() {
        this.cookieStore = new BasicCookieStore();
        this.client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(60000)
//                .setProxy(new HttpHost("127.0.0.1", 8888, "http"))
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(60000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(60000).setProxy(HttpHost.create("http://127.0.0.1:1080"))
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();
    }

    /**
     * 模拟Http Get请求
     *
     * @param urlStr   请求路径
     * @param paramMap 请求参数
     * @return
     * @throws Exception
     */
    public CloseableHttpResponse get(String urlStr, Map<String, String> paramMap) throws Exception {
        if (paramMap != null && paramMap.size() != 0) {
            urlStr = urlStr + "?" + getParamString(paramMap);
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(urlStr);
        // 响应模型
        CloseableHttpResponse response = null;
        // 配置信息

        // 将上面的配置信息 运用到这个Get请求里
        httpGet.setConfig(requestConfig);
        // 由客户端执行(发送)Get请求
        try {
            response = client.execute(httpGet);
        } finally {
//            try {
//                if (response != null) {
//                    response.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return response;
    }

    /**
     * 模拟Http Post请求
     *
     * @param urlStr 请求路径
     * @param map    请求参数
     * @return
     * @throws Exception
     */
    public CloseableHttpResponse post(String urlStr, Map<String, Object> map) throws Exception {
        String jsonString = JSON.toJSONString(map);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        HttpPost httpPost = new HttpPost(urlStr);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 将上面的配置信息 运用到这个Get请求里
        httpPost.setConfig(requestConfig);
        // 响应模型
        CloseableHttpResponse response = null;
        HttpEntity responseEntity = null;
        try {
            // 由客户端执行(发送)Post请求
            response = client.execute(httpPost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (response != null) {
//                    response.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return response;
    }

    /**
     * 将参数转为路径字符串
     *
     * @param paramMap 参数
     * @return
     */
    private static String getParamString(Map<String, String> paramMap) {
        if (null == paramMap || paramMap.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String key : paramMap.keySet()) {
            builder.append("&")
                    .append(key).append("=").append(paramMap.get(key));
        }
        return builder.deleteCharAt(0).toString();
    }

    public void download(String urlStr, Map<String, String> paramMap, String filePath) throws Exception {
        String jsonString = JSON.toJSONString(paramMap);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        HttpPost httpPost = new HttpPost(urlStr);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        HttpEntity httpEntity = null;
        try {
            // 由客户端执行(发送)Post请求
            response = client.execute(httpPost);
            // 从响应模型中获取响应实体
            httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            httpEntity.writeTo(fos);
            is.close();
            fos.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public void setClient(CloseableHttpClient client) {
        this.client = client;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
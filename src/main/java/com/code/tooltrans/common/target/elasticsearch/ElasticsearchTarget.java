package com.code.tooltrans.common.target.elasticsearch;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.target.AbstractTarget;
import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 11:06
 */
public class ElasticsearchTarget extends AbstractTarget {
    private JestClient client;

    public ElasticsearchTarget(Properties properties) {
        client = getJestClient(properties.getProperty("esIp"), properties.getProperty("esPort"));
    }

    public ElasticsearchTarget(String ip, String port) {
        client = getJestClient(ip, port);
    }

    private JestClient getJestClient(String ip, String port) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://" + ip + ":" + port)
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create())
                .connTimeout(3000)
                .readTimeout(3000)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }

    /**
     * 索引文档
     *
     * @param jestClient
     * @param indexName
     * @param objs
     * @return
     * @throws Exception
     */
    private boolean index(JestClient jestClient, String indexName, List<DataRowModel> objs) {
        Bulk.Builder bulk = new Bulk.Builder().defaultIndex(indexName).defaultType("_doc");
        for (DataRowModel obj : objs) {
            Index index = new Index.Builder(obj.getProperties()).id(obj.getId()).build();
            bulk.addAction(index);
        }
        bulk.refresh(true);
        BulkResult br = null;
        try {
            br = jestClient.execute(bulk.build());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("写入失败", e);
        }
        if (!br.isSucceeded()) {
            throw new RuntimeException("写入失败:\n" +
                    (br.getFailedItems().size() > 0 ? br.getFailedItems().get(0).errorReason : br.getErrorMessage()));
        }
        return br.isSucceeded();
    }

    @Override
    public boolean save(List<DataRowModel> docs, String indexName) {
        return index(client, indexName, docs);
    }

    @Override
    public boolean saveOrUpdate(List<DataRowModel> docs, String indexName) {
        throw new RuntimeException("暂不支持");
    }

    @Override
    public boolean close() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException("关闭异常");
        }
        return true;
    }
}

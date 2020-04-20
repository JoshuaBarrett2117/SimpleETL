package common.target;

import com.code.common.dao.model.DomainElement;
import com.google.gson.GsonBuilder;
import common.IDataTarget;
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
public class ElasticsearchTarget implements IDataTarget {
    private JestClient client;

    public ElasticsearchTarget(Properties properties) {
        client = getJestClient(properties.getProperty("esIp"), properties.getProperty("esPort"));
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
     * @param typeName
     * @param objs
     * @return
     * @throws Exception
     */
    private boolean index(JestClient jestClient, String indexName, String typeName, List<DomainElement> objs) {
        Bulk.Builder bulk = new Bulk.Builder().defaultIndex(indexName).defaultType(typeName);
        for (DomainElement obj : objs) {
            Index index = new Index.Builder(obj.getProperties()).build();
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
    public boolean save(List<DomainElement> docs, String indexName) {
        return index(client, indexName, indexName, docs);
    }
}

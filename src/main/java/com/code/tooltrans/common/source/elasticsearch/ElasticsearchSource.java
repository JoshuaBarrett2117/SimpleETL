package com.code.tooltrans.common.source.elasticsearch;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.IDataSource;
import com.google.gson.*;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.ClearScroll;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchScroll;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/4/7 13:35
 */
public class ElasticsearchSource implements IDataSource {
    private JestClient client;
    private static final String TIME = "1h";
    private Gson gson = new Gson();

    public ElasticsearchSource(String ip, String port) {
        client = getJestClient(ip, port);
    }

    public ElasticsearchSource(Properties properties) {
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

    @Override
    public DataRowModel queryForObject(Exp sql) {
        Search.Builder builder = new Search.Builder(sql.getExp());
        builder.addIndices(sql.getTableNames());
        try {
            SearchResult execute = client.execute(builder.build());
            if (!execute.isSucceeded()) {
                throw new RuntimeException(execute.getErrorMessage());
            }
            SearchResult.Hit<Map, Void> hit = execute.getFirstHit(Map.class);
            if (hit == null) {
                return null;
            }
            return wrapDomainElement(hit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<DataRowModel> iterator(Exp sql) {
        Search.Builder builder = new Search.Builder(sql.getExp());
        builder.setParameter("scroll", TIME);
        builder.addIndices(sql.getTableNames());
        String scrollId;

        List<DataRowModel> result = new ArrayList<>();
        try {
            SearchResult execute = client.execute(builder.build());
            if (!execute.isSucceeded()) {
                throw new RuntimeException(execute.getErrorMessage());
            }
            scrollId = execute.getValue("_scroll_id").toString();
            List<SearchResult.Hit<Map, Void>> hits = execute.getHits(Map.class);
            for (SearchResult.Hit<Map, Void> hit : hits) {
                DataRowModel dataRowModel = wrapDomainElement(hit);
                result.add(dataRowModel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Iterator<DataRowModel>() {
            Iterator<DataRowModel> tempIterator = result.iterator();
            String scroll_id = scrollId;

            @Override
            public boolean hasNext() {
                if (tempIterator.hasNext()) {
                    return true;
                } else if (this.scroll_id != null) {
                    try {
                        List<DataRowModel> result = nextResource();
                        if (result.size() > 0) {
                            tempIterator = result.iterator();
                            return true;
                        } else {
                            clearScroll(this.scroll_id);
                            this.scroll_id = null;
                            return false;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return false;
                }
            }

            @NotNull
            private List<DataRowModel> nextResource() throws IOException {
                List<DataRowModel> result = new ArrayList<>();
                SearchScroll.Builder scroll = new SearchScroll.Builder(scroll_id, TIME);
                JestResult execute = client.execute(scroll.build());
                JsonObject jsonObject = execute.getJsonObject();
                JsonPrimitive scroll_id = jsonObject.getAsJsonPrimitive("_scroll_id");
                if (scroll_id == null) {
                    System.out.println(1);
                }
                this.scroll_id = scroll_id.getAsString();
                JsonArray hits = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                for (JsonElement hit : hits) {
                    DataRowModel dataRowModel = new DataRowModel();
                    dataRowModel.setId(hit.getAsJsonObject().getAsJsonPrimitive("_id").getAsString());
                    dataRowModel.setType(hit.getAsJsonObject().getAsJsonPrimitive("_type").getAsString());
                    JsonObject source = hit.getAsJsonObject().getAsJsonObject("_source");
                    Map<String, Object> map = gson.fromJson(source.toString(), Map.class);
                    dataRowModel.setProperties(map);
                    result.add(dataRowModel);
                }
                return result;
            }

            @Override
            public DataRowModel next() {
                return tempIterator.next();
            }

            private void clearScroll(String scrollId) {
                ClearScroll.Builder clearScroll = new ClearScroll.Builder();
                clearScroll.addScrollId(scrollId);
                try {
                    client.execute(clearScroll.build());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }

    @NotNull
    private DataRowModel wrapDomainElement(SearchResult.Hit<Map, Void> hit) {
        DataRowModel dataRowModel = new DataRowModel();
        dataRowModel.setId(hit.id);
        dataRowModel.setType(hit.type);
        dataRowModel.setScore(hit.score);
        dataRowModel.setProperties(hit.source);
        return dataRowModel;
    }
}

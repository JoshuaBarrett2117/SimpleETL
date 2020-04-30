package common.source;

import com.code.common.dao.model.DomainElement;
import com.google.gson.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import common.IDataSource;
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
    public Iterator<DomainElement> iterator(Exp sql) {
        Search.Builder builder = new Search.Builder(sql.getExp());
        builder.setParameter("scroll", TIME);
        builder.addIndices(sql.getTableNames());
        String scrollId;

        List<DomainElement> result = new ArrayList<>();
        try {
            SearchResult execute = client.execute(builder.build());
            if(!execute.isSucceeded()){
                throw new RuntimeException(execute.getErrorMessage());
            }
            scrollId = execute.getValue("_scroll_id").toString();
            List<SearchResult.Hit<Map, Void>> hits = execute.getHits(Map.class);
            for (SearchResult.Hit<Map, Void> hit : hits) {
                DomainElement domainElement = new DomainElement();
                domainElement.setId(hit.id);
                domainElement.setType(hit.type);
                domainElement.setProperties(hit.source);
                result.add(domainElement);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Iterator<DomainElement>() {
            Iterator<DomainElement> tempIterator = result.iterator();
            String scroll_id = scrollId;

            @Override
            public boolean hasNext() {
                if (tempIterator.hasNext()) {
                    return true;
                } else if (this.scroll_id != null) {
                    try {
                        List<DomainElement> result = nextResource();
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
            private List<DomainElement> nextResource() throws IOException {
                List<DomainElement> result = new ArrayList<>();
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
                    DomainElement domainElement = new DomainElement();
                    domainElement.setId(hit.getAsJsonObject().getAsJsonPrimitive("_id").getAsString());
                    domainElement.setType(hit.getAsJsonObject().getAsJsonPrimitive("_type").getAsString());
                    JsonObject source = hit.getAsJsonObject().getAsJsonObject("_source");
                    Map<String, Object> map = gson.fromJson(source.toString(), Map.class);
                    domainElement.setProperties(map);
                    result.add(domainElement);
                }
                return result;
            }

            @Override
            public DomainElement next() {
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
}

package server;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticsearchClientFactory {

    public static ElasticsearchClient getClient() {
        val restClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        val transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}

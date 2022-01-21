import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {

        val scanner = new Scanner(System.in);

        val restClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        val transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        val client = new ElasticsearchClient(transport);


        while (true) {
            System.out.println("Input query (e to exit): ");
            var query = scanner.nextLine();
            System.out.println(query);
            if (query.equals("e")) {
                break;
            }

            SearchResponse<BookSearchResult> search = client.search(s -> s
                            .index("book")
                            .query(q -> q
                                    .queryString(qs -> qs
                                            .fields(List.of("content"))
                                            .query(query)
                                    )),
                    BookSearchResult.class);

            System.out.println("Found " + search.hits().hits().size() + " matches");

            for (Hit<BookSearchResult> hit : search.hits().hits()) {
                System.out.println(hit.source().getTitle());
                System.out.println(hit.source().getLineNumber());
                System.out.println(hit.source().getContent());
                System.out.println("----");
            }
        }
        restClient.close();
    }
}

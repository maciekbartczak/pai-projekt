
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                ));
        var scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Input query (e to exit): ");
            var query = scanner.nextLine();
            if (query.equals("e")) {
                break;
            }

            SearchRequest searchRequest = new SearchRequest("book");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.queryStringQuery(query));
            searchSourceBuilder.size(10);

            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println("Total hits: " + searchResponse.getHits().getTotalHits() + "for query: " + query);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                System.out.println(hit.getSourceAsString());
            }
        }
        client.close();
    }
}

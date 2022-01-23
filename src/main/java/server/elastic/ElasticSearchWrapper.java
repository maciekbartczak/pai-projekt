package server.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import common.BookSearchResult;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ElasticSearchWrapper {
    private final ElasticsearchClient client;

    public List<Hit<BookSearchResult>> search(String query) {
        SearchResponse<BookSearchResult> search;
        try {
            search = client.search(s -> s
                            .index("book")
                            .query(q -> q
                                    .queryString(qs -> qs
                                            .fields(List.of("content"))
                                            .query(query)
                                    )),
                    BookSearchResult.class);
            return search.hits().hits();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

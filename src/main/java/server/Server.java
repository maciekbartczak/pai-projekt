package server;

import lombok.val;
import server.elastic.ElasticSearchWrapper;
import server.elastic.ElasticsearchClientFactory;
import server.user.UserRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        val elasticsearchClient = ElasticsearchClientFactory.getClient();
        val elasticsearchWrapper = new ElasticSearchWrapper(elasticsearchClient);
        val userRepository = new UserRepository("test.xml");

        try (val server = new ServerSocket(1234, 0, InetAddress.getByName(null))) {
            System.out.println("Server running at " + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort());
            while (true) {
                Socket client = server.accept();
                ClientHandler clientHandler = new ClientHandler(client, elasticsearchWrapper, userRepository);
                clientHandler.start();
            }
        }
    }
}



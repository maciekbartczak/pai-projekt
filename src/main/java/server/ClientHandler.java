package server;

import common.BookSearchResult;
import common.Packet;
import common.SearchPacketContent;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClientHandler extends Thread {

    private final Socket socket;
    private final ElasticSearchWrapper elasticSearchWrapper;


    @Override
    public void run() {
        try {
            val input = new ObjectInputStream(socket.getInputStream());
            val output = new ObjectOutputStream(socket.getOutputStream());

            Packet packet;
            while((packet = (Packet) input.readObject()) != null) {
                switch (packet.type) {
                    case REGISTER:
                        break;
                    case LOGIN:
                        break;
                    case SEARCH:
                        handleSearchPacket(packet, output);
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSearchPacket(Packet packet, ObjectOutputStream output) throws IOException {
        val content = (SearchPacketContent) packet.content;
        val searchResult = elasticSearchWrapper.search(content.query)
                .stream()
                .map(hit -> convertBookSearchResultToString(hit.source()))
                .collect(Collectors.toList());
        output.writeObject(searchResult);
    }

    private String convertBookSearchResultToString(BookSearchResult bookSearchResult) {
        return  "Title: " + bookSearchResult.getTitle() + "\n" +
                "Line: " + bookSearchResult.getLineNumber() + "\n" +
                "Content: " + bookSearchResult.getContent() + "\n";
    }
}

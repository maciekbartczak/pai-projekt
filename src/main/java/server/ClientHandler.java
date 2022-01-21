package server;

import common.BookSearchResult;
import common.Packet;
import common.SearchPacketContent;
import common.UserData;
import lombok.RequiredArgsConstructor;
import lombok.val;
import server.user.UserDataEntries;
import server.user.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClientHandler extends Thread {

    private final Socket socket;
    private final ElasticSearchWrapper elasticSearchWrapper;
    private final UserRepository userRepository;
    private boolean authenticated = false;

    @Override
    public void run() {
        try {
            val input = new ObjectInputStream(socket.getInputStream());
            val output = new ObjectOutputStream(socket.getOutputStream());

            Packet packet;
            while((packet = (Packet) input.readObject()) != null) {
                switch (packet.type) {
                    case REGISTER:
                        handleRegisterPacket(packet, output);
                        break;
                    case LOGIN:
                        handleLoginPacket(packet, output);
                        break;
                    case SEARCH:
                        if (!authenticated) {
                            output.writeObject("Log in to do that!");
                        } else {
                            handleSearchPacket(packet, output);
                        }
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

    private void handleLoginPacket(Packet packet, ObjectOutputStream output) throws IOException {
        val content = (UserData) packet.content;
        val users = userRepository.findAll().getUserDataEntry();
        val user = new UserData(content.getUsername(), content.getPassword());
        if (matchCredentials(users, user)) {
            this.authenticated = true;
            output.writeObject("Logged in!");
        } else {
            output.writeObject("Username or password invalid!");
        }
    }

    private boolean matchCredentials(List<UserData> users, UserData user) {
        for (UserData userData : users) {
            if (Objects.equals(userData.getUsername(), user.getUsername())
            && Objects.equals(userData.getPassword(), user.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private void handleRegisterPacket(Packet packet, ObjectOutputStream output) throws IOException {
        val content = (UserData) packet.content;
        val users = Optional.ofNullable(userRepository.findAll().getUserDataEntry())
                .orElse(new ArrayList<>());
        val newUser = new UserData(content.getUsername(), content.getPassword());
        if (users.contains(newUser)) {
            output.writeObject("User exists!");
            return;
        }
        users.add(newUser);
        userRepository.saveAll(new UserDataEntries(users));
        output.writeObject("Registered! Now you can log in.");
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

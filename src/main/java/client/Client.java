package client;

import common.CredentialsPacketContent;
import common.Packet;
import common.SearchPacketContent;
import lombok.val;
import server.user.UserData;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean authenticated = false;

    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", 1234)) {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            for (;;) {
                if (!authenticated) {
                    userAuthenticate();
                } else {
                    userSearch();
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void userAuthenticate() throws IOException, ClassNotFoundException {
        System.out.println("1. Register");
        System.out.println("2. Login");
        val choice = scanner.nextLine();
        if (choice.equals("1")) {
            System.out.print("Username: ");
            val username = scanner.nextLine();
            System.out.print("Password: ");
            val password = scanner.nextLine();
            val userData = new UserData(username, password);
            val packet = new Packet(Packet.PacketType.REGISTER, new CredentialsPacketContent(userData));
            output.writeObject(packet);

            String response = (String) input.readObject();
            System.out.println(response);
        } else if (choice.equals("2")) {
            System.out.print("Username: ");
            val username = scanner.nextLine();
            System.out.print("Password: ");
            val password = scanner.nextLine();
            val userData = new UserData(username, password);
            val packet = new Packet(Packet.PacketType.LOGIN, new CredentialsPacketContent(userData));
            output.writeObject(packet);

            String response = (String) input.readObject();
            System.out.println(response);
            if (response.equals("Logged in!")) {
                authenticated = true;
            }
        } else {
            System.out.println("Unknown option!");
        }

    }

    private static void userSearch() throws IOException, ClassNotFoundException {
        System.out.println("Input query");
        val query = scanner.nextLine();
        val packet = new Packet(Packet.PacketType.SEARCH, new SearchPacketContent(query));
        output.writeObject(packet);

        List<String> searchResult = (List<String>) input.readObject();
        for (String hit : searchResult) {
            System.out.println(hit);
        }
    }

}

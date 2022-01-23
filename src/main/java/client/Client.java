package client;

import common.Packet;
import common.RemindPasswordPacketContent;
import common.SearchPacketContent;
import common.UserData;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        System.out.println("3. Remind password");
        val choice = scanner.nextLine();
        if (choice.equals("1")) {
            System.out.print("Username: ");
            val username = scanner.nextLine();
            System.out.print("Password: ");
            val password = scanner.nextLine();
            val userData = new UserData(username, password);
            val packet = new Packet(Packet.PacketType.REGISTER, userData);
            output.writeObject(packet);

            String response = (String) input.readObject();
            System.out.println(response);
        } else if (choice.equals("2")) {
            System.out.print("Username: ");
            val username = scanner.nextLine();
            System.out.print("Password: ");
            val password = scanner.nextLine();
            val userData = new UserData(username, password);
            val packet = new Packet(Packet.PacketType.LOGIN, userData);
            output.writeObject(packet);

            String response = (String) input.readObject();
            System.out.println(response);
            if (response.equals("Logged in!")) {
                authenticated = true;
            }
        } else if (choice.equals("3")) {
            System.out.print("Username: ");
            val username = scanner.nextLine();
            val packet = new Packet(Packet.PacketType.REMIND_PASSWORD, new RemindPasswordPacketContent(username));

            output.writeObject(packet);

            System.out.println(input.readObject());
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

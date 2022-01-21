package client;

import common.Packet;
import common.SearchPacketContent;
import lombok.val;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", 1234)) {
            val output = new ObjectOutputStream(socket.getOutputStream());
            val input = new ObjectInputStream(socket.getInputStream());
            val scanner = new Scanner(System.in);

            for (;;) {
                System.out.println("Input query");
                val query = scanner.nextLine();
                val packet = new Packet(Packet.PacketType.SEARCH, new SearchPacketContent(query));
                output.writeObject(packet);

                List<String> searchResult = (List<String>) input.readObject();
                for (String hit : searchResult) {
                    System.out.println(hit);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

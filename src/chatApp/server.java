package chatApp;

import java.io.*;
import java.net.*;

public class server {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    public server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection...");
            System.out.println("Waiting for client to connect...");

            socket = server.accept(); // waits for client connection
            System.out.println("Client connected.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // auto flush

            startRead();
            startWrite();

        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void startRead() {
        Runnable r1 = () -> {
            System.out.println("Reader thread started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equalsIgnoreCase("exit")) {
                        System.out.println("Client terminated the chat.");
                        socket.close();
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start(); // ✅ use start()
    }

    public void startWrite() {
        Runnable r2 = () -> {
            System.out.println("Writer thread started...");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                while (!socket.isClosed()) {
                    String content = br1.readLine();
                    out.println(content);

                    if (content.equalsIgnoreCase("exit")) {
                        socket.close();
                        System.out.println("You ended the chat.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Writer stopped.");
            }
        };
        new Thread(r2).start(); // ✅ use start()
    }

    public static void main(String[] args) {
        System.out.println("Starting the server...");
        new server();
    }
}

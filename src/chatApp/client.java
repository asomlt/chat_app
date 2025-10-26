package chatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public client() {
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection established.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // autoFlush = true

            startRead();
            startWrite();

        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Reading messages from server
    public void startRead() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equalsIgnoreCase("exit")) {
                        System.out.println("Server terminated the chat.");
                        socket.close();
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start(); // ✅ Use start(), not run()
    }

    // Writing messages to server
    public void startWrite() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                while (!socket.isClosed()) {
                    String content = br1.readLine();
                    out.println(content);

                    if (content.equalsIgnoreCase("exit")) {
                        socket.close();
                        System.out.println("You terminated the chat.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Writer stopped.");
            }
        };
        new Thread(r2).start(); // ✅ Use start(), not run()
    }

    public static void main(String[] args) {
        System.out.println("This is client side...");
        new client();
    }
}

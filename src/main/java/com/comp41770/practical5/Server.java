package com.comp41770.practical5;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// TODO: Handle multiple clients
@SuppressWarnings("ThrowablePrintedToSystemOut")
public class Server {
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on " + serverSocket.getLocalSocketAddress() + ":" + port);
            System.out.println("Waiting for a client...");
            socket = serverSocket.accept();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String message = "";

            while(!message.equals("Over")) {
                try {
                    message = in.readUTF();
                    System.out.println(message);
                } catch (IOException exception) {
                    System.out.println(exception);
                }
            }

            System.out.println("Closing connection");
            socket.close();
            in.close();
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}

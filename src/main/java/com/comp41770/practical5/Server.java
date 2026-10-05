package com.comp41770.practical5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// TODO: Handle multiple clients
@SuppressWarnings("ThrowablePrintedToSystemOut")
public class Server {
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private static int messageCounter = 0;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on " + serverSocket.getLocalSocketAddress() + ":" + port);
            System.out.println("Waiting for a client...");

            // IntelliJ HATES while(true)
            while(true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException exception) {
                    System.out.println(exception);
                }
                new ServerThread(socket).start();
            }
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    public static void incrementCounter() {
        messageCounter++;
    }

    public static int getMessageCounter() {
        return messageCounter;
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}

class ServerThread extends Thread {
    protected Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        DataInputStream in;
        DataOutputStream out;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            System.out.println(exception);
            return;
        }

        String message;

        while(socket.isConnected()) {
            try {
                message = in.readUTF();
                if(message.equalsIgnoreCase("exit")) {
                    System.out.println("Client closed connection");
                    System.out.println("Server total message count: " + Server.getMessageCounter() + " messages.");
                    socket.close();
                    return;
                } else {
                    System.out.println("Client " + Thread.currentThread().getId() + ": " + message);
                    Server.incrementCounter();
                    out.writeBytes(message + "\n");
                    out.flush();
                }
            } catch (IOException exception) {
                System.out.println(exception);
                return;
            }
        }
    }
}
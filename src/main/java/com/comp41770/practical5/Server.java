package com.comp41770.practical5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"ThrowablePrintedToSystemOut", "InfiniteLoopStatement"})
public class Server {
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private static int messageCounter = 0;

    private static final String DIRECTORY_HOST = "127.0.0.1";
    private static final int DIRECTORY_PORT = 9000;
    private static final List<ServerThread> CLIENTS = new CopyOnWriteArrayList<>();

    private Socket registrationSocket = null;
    private final String serverName;
    private final int serverPort;

    public Server(String name, int port) {
        this.serverName = name;
        this.serverPort = port;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server \"" + serverName + "\" listening on " + serverSocket.getLocalSocketAddress() + ":" + serverPort);
            System.out.println("Server \"" + serverName + "\" waiting for a client...");

            registerWithDirectory();

            // IntelliJ HATES while(true)
            while(true) {
                try {
                    socket = serverSocket.accept();
                    ServerThread clientThread = new ServerThread(socket);
                    CLIENTS.add(clientThread);
                    clientThread.start();
                } catch (IOException exception) {
                    System.out.println(exception);
                }
            }
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            closeRegistration();
        }
    }

    private void registerWithDirectory() {
        try {
            registrationSocket = new Socket(DIRECTORY_HOST, DIRECTORY_PORT);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(registrationSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(registrationSocket.getInputStream(), StandardCharsets.UTF_8));

            writer.write("REGISTER " + serverName + " " + DIRECTORY_HOST + ":" + serverPort);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();

            if(response.equalsIgnoreCase("ok")) {
                System.out.println(serverName + " registered with directory " + DIRECTORY_HOST + ":" + DIRECTORY_PORT);
            } else {
                System.out.println(serverName + " failed to register with directory: " + response);
            }
        } catch (IOException exception) {
            System.out.println(serverName + " failed to register with directory: " + exception);
            registrationSocket = null;
        }
    }

    private void closeRegistration() {
        if(registrationSocket != null) {
            try {
                registrationSocket.close();
            } catch (IOException exception) {
                System.out.println(exception);
            }
            registrationSocket = null;
        }
    }

    public static void incrementCounter() {
        messageCounter++;
    }

    public static int getMessageCounter() {
        return messageCounter;
    }

    public static void removeClient(ServerThread client) {
        CLIENTS.remove(client);
    }

    public static void broadcast(String message) {
        for(ServerThread client : CLIENTS) {
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java Server <name> <port>");
            System.out.println("Example: java Server srv1 9101");
            return;
        }
        String name = args[0];
        int port;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid port: " + args[1]);
            return;
        }
        new Server(name, port);
    }
}

@SuppressWarnings("ThrowablePrintedToSystemOut")
class ServerThread extends Thread {
    protected Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Server.broadcast("Client " + Thread.currentThread().getId() + " connected to the server.");

            String message;

            while(true) {
                message = in.readUTF();

                if(message.equalsIgnoreCase("exit")) {
                    System.out.println("Client closed connection");
                    System.out.println("Server total message count: " + Server.getMessageCounter() + " messages.");
                    Server.broadcast("Client " + Thread.currentThread().getId() + " disconnected from the server.");
                    return;
                } else {
                    String broadcastMessage = "Client " + Thread.currentThread().getId() + ": " + message;
                    System.out.println(broadcastMessage);
                    Server.incrementCounter();
                    Server.broadcast(broadcastMessage);
                }
            }
        } catch(IOException exception) {
            System.out.println(exception);
        } finally {
            Server.removeClient(this);

            try {
                socket.close();
            } catch(IOException exception) {
                System.out.println(exception);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch(IOException exception) {
            System.out.println(exception);
            Server.removeClient(this);
        }
    }
}
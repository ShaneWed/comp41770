package com.comp41770.practical5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryServer {
    private static final String HOST = "127.0.0.1";
    private static final int DIR_PORT = 9000;
    private static final int BUFFER = 1024;

    private static final List<ServerEntry> REGISTRY = new CopyOnWriteArrayList<>();

    static class ServerEntry {
        String name;
        String host;
        int port;

        ServerEntry(String name, String host, int port) {
            this.name = name;
            this.host = host;
            this.port = port;
        }
    }

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(DIR_PORT)) {
            System.out.println("Directory server listening at " + HOST + ":" + DIR_PORT);

            while(true) {
                Socket socket = serverSocket.accept();
                new DirectoryClientThread(socket).start();
            }
        } catch(IOException exception) {
            System.out.println(exception);
        }
    }

    private static void registerServer(String name, String host, int port) {
        REGISTRY.removeIf(entry -> entry.name.equalsIgnoreCase(name));
        REGISTRY.add(new ServerEntry(name, host, port));
    }

    private static class DirectoryClientThread extends Thread {
        private final Socket socket;

        DirectoryClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Socket clientSocket = socket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

                String request = reader.readLine();

                if(request == null) {
                    return;
                }
                if(request.startsWith("REGISTER")) {
                    handleRegister(request, writer);
                } else if (request.equalsIgnoreCase("LIST")) {
                    handleList(writer);
                } else {
                    writer.write("Unknown command...");
                    writer.newLine();
                    writer.flush();
                }
            } catch(IOException exception) {
                System.out.println(exception);
            }
        }

        private void handleRegister(String request, BufferedWriter writer) throws IOException {
            String[] parts = request.split("\\s+", 3);

            if(parts.length != 3 || !parts[2].contains(":")) {
                writer.write("ERROR - Incorrect arguments - Usage: REGISTER <name> <host>:<port>");
                writer.newLine();
                writer.flush();
                return;
            }

            String name = parts[1];
            String[] addressParts = parts[2].split(":", 2);
            String host = addressParts[0];

            int port;
            try {
                port = Integer.parseInt(addressParts[1]);
            } catch(NumberFormatException exception) {
                writer.write("ERROR - Invalid port...");
                writer.newLine();
                writer.flush();
                return;
            }

            registerServer(name, host, port);

            System.out.println("Registered server: " + name + " at " + host + ":" + port);

            writer.write("OK");
            writer.newLine();
            writer.flush();
        }

        private void handleList(BufferedWriter writer) throws IOException {
            for(ServerEntry entry : REGISTRY) {
                writer.write(entry.name + " " + entry.host + ":" + entry.port);
                writer.newLine();
            }

            writer.write("END");
            writer.newLine();
            writer.flush();
        }
    }
}

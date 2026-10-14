package com.comp41770.practical5;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class Client {
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private static final String DIRECTORY_HOST = "127.0.0.1";
    private static final int DIRECTORY_PORT = 9000;

    private static class ServerInfo {
        String name;
        String host;
        int port;

        ServerInfo(String name, String host, int port) {
            this.name = name;
            this.host = host;
            this.port = port;
        }
    }

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to " + address + ":" + port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            System.out.println(exception);
            return;
        }

        Thread listenerThread = new Thread(() -> {
            while(!socket.isClosed()) {
                try {
                    String response = in.readUTF();
                    System.out.println(response);
                    System.out.print("> ");
                } catch(IOException exception) {
                    if(!socket.isClosed()) {
                        System.out.println(exception);
                    }
                    break;
                }
            }
        });

        listenerThread.start();

        Scanner scanner = new Scanner(System.in);
        String message = "";

        while(!message.equalsIgnoreCase("exit")) {
            try {
                System.out.print("> ");
                message = scanner.nextLine();
                out.writeUTF(message);
                out.flush();

                if(message.equalsIgnoreCase("exit")) {
                    break;
                }
            } catch (IOException exception) {
                System.out.println(exception);
                break;
            }
        }

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    private static List<ServerInfo> getServers() {
        List<ServerInfo> servers = new ArrayList<>();

        try {
            Socket directorySocket = new Socket(DIRECTORY_HOST, DIRECTORY_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(directorySocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(directorySocket.getOutputStream(), StandardCharsets.UTF_8));

            writer.write("LIST");
            writer.newLine();
            writer.flush();

            String line;
            while((line = reader.readLine()) != null && !line.equalsIgnoreCase("END")) {
                String[] parts = line.split("\\s+", 2);

                // TODO
                if(parts.length != 2 || !parts[1].contains(":")) {
                    continue;
                }

                String name = parts[0];
                String[] addressParts = parts[1].split(":", 2);
                String host = addressParts[0];

                try {
                    int port = Integer.parseInt(addressParts[1]);
                    servers.add(new ServerInfo(name, host, port));
                } catch(NumberFormatException exception) {
                    System.out.println(exception);
                }
            }
        } catch (IOException exception) {
            System.out.println(exception);
            return null;
        }

        return servers;
    }

    private static ServerInfo chooseServer(List<ServerInfo> servers) {
        if(servers == null || servers.isEmpty()) {
            System.out.println("No servers are available...");
            return null;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Available servers:");
        for(int i = 0; i < servers.size(); i++) {
            ServerInfo server = servers.get(i);
            System.out.println((i + 1) + ". " + server.name + " at " + server.host + ":" + server.port);
        }

        while(true) {
            System.out.print("Select a server: ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                if(choice >= 1 && choice <= servers.size()) {
                    return servers.get(choice - 1);
                }
            } catch(NumberFormatException exception) {
                System.out.println("Invalid selection. Try again... ");
                continue;
            }

            System.out.println("Invalid selection. Try again... ");
        }
    }

    public static void main(String[] args) {
        List<ServerInfo> servers = getServers();
        ServerInfo selectedServer = chooseServer(servers);

        if(selectedServer == null) {
            System.out.println("Selected server is null!");
            return;
        }

        new Client(selectedServer.host, selectedServer.port);
    }
}

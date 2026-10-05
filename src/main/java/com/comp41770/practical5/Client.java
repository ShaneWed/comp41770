package com.comp41770.practical5;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class Client {
    private Socket socket = null;
    private InputStreamReader in = null;
    private DataOutputStream out = null;
    private BufferedReader reader = null;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to " + address + ":" + port);
            in = new InputStreamReader(System.in);
            out = new DataOutputStream(socket.getOutputStream());
            reader = new BufferedReader(in);
        } catch (IOException exception) {
            System.out.println(exception);
        }

        String message = "";

        while(!message.equalsIgnoreCase("exit")) {
            try {
                if (reader != null) {
                    message = reader.readLine();
                } else {
                    System.out.println("ERROR: Message was null...");
                }
                if (out != null) {
                    out.writeUTF(message);
                    out.flush();
                } else {
                    System.out.println("ERROR: Message was null...");
                }
            } catch (IOException exception) {
                System.out.println(exception);
            }
        }

        try {
            reader.close();
            out.close();
            socket.close();
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5000);
    }
}

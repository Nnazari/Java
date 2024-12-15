package com.Chat;

import com.Chat.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String authToken;

    public Client() {
        try {
            socket = new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            authenticate();
            if (authToken != null) {
                selectChat();
                handleMessages();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void authenticate() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println(in.readLine());
        String username = scanner.nextLine();
        out.println(username);

        System.out.println(in.readLine());
        String password = scanner.nextLine();
        out.println(password);


        String response = in.readLine();
        String[] parts = response.split(Constants.MESSAGE_SEPARATOR);
        if (parts[0].equals(Constants.AUTH_SUCCESS)) {
            this.authToken = parts[1];
            System.out.println("Authentication success");
        } else {
            System.out.println("Authentication failed");
        }
    }

    private void selectChat() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String response = in.readLine();
        String[] parts = response.split(Constants.MESSAGE_SEPARATOR);
        if (parts[0].equals(Constants.CHAT_LIST)){
            System.out.println("Available chats:");
            String chats = parts[1];
            if (chats.equalsIgnoreCase("No chats available")){
                System.out.println(chats);
                return;
            }

            String[] chatsArray = chats.split(Constants.DATA_SEPARATOR);
            for (int i = 0; i < chatsArray.length; i+=2){
                System.out.println("Id: " + chatsArray[i] + " Name: " + chatsArray[i + 1]);
            }

            System.out.println(in.readLine());
            out.println(scanner.nextLine());
            String joinResponse = in.readLine();
            if (joinResponse.equals(Constants.CHAT_JOIN_SUCCESS)) {
                System.out.println("Joined to chat successfully");
            } else {
                System.out.println("Can't join to chat");
            }

        }

    }

    private void handleMessages() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String response = in.readLine();
            String[] parts = response.split(Constants.MESSAGE_SEPARATOR);
            if (parts[0].equals(Constants.NEW_MESSAGE)) {
                String messages = parts[1];
                if (!messages.isEmpty()) {
                    String[] messageArray = messages.split(Constants.DATA_SEPARATOR);
                    Arrays.stream(messageArray).forEach(System.out::println);
                }
            }
            System.out.println("Enter message or exit:");
            String message = scanner.nextLine();
            out.println(message);
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}

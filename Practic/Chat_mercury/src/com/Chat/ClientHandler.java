package com.Chat;

import com.Chat.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataBaseManager databaseManager;
    private BufferedReader in;
    private PrintWriter out;
    private User user;
    private int currentChatId;
    private String authToken;


    public ClientHandler(Socket socket, DataBaseManager databaseManager) {
        this.clientSocket = socket;
        this.databaseManager = databaseManager;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            authenticateClient();
            if (authToken != null) {
                handleChatSelection();
                if (currentChatId != 0) {
                    handleMessages();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    private void authenticateClient() throws IOException {
        out.println("Enter username");
        String username = in.readLine();
        out.println("Enter password");
        String password = in.readLine();

        User authenticatedUser = databaseManager.authenticate(username, password);
        if (authenticatedUser != null) {
            this.user = authenticatedUser;
            this.authToken = UUID.randomUUID().toString();
            out.println(Constants.AUTH_SUCCESS + Constants.MESSAGE_SEPARATOR + authToken);
            System.out.println("User " + user.getUsername() + " authenticated");
        } else {
            out.println(Constants.AUTH_FAIL);
            System.out.println("Authentication failed for user " + username);
        }

    }


    private void handleChatSelection() throws IOException {
        List<Chat> chats = databaseManager.getChats(user.getId());
        if (chats.isEmpty()) {
            out.println(Constants.CHAT_LIST + Constants.MESSAGE_SEPARATOR + "No chats available");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Chat chat : chats) {
            sb.append(chat.getId()).append(Constants.DATA_SEPARATOR).append(chat.getName()).append(Constants.DATA_SEPARATOR);
        }
        out.println(Constants.CHAT_LIST + Constants.MESSAGE_SEPARATOR + sb);
        out.println("Select chat by ID");
        int chatId = Integer.parseInt(in.readLine());
        if (databaseManager.isUserInChat(user.getId(), chatId)) {
            this.currentChatId = chatId;
            out.println(Constants.CHAT_JOIN_SUCCESS);
            System.out.println("User " + user.getUsername() + " joined to chat " + chatId);
        } else {
            out.println(Constants.CHAT_JOIN_FAIL);
            System.out.println("User " + user.getUsername() + " is not member of chat " + chatId);
        }

    }


    private void handleMessages() throws IOException {
        while(true) {
            List<Message> messages = databaseManager.getMessages(currentChatId);
            StringBuilder sb = new StringBuilder();
            for (Message message : messages) {
                sb.append(message.toString()).append(Constants.DATA_SEPARATOR);
            }
            out.println(Constants.NEW_MESSAGE + Constants.MESSAGE_SEPARATOR + sb.toString());
            String message = in.readLine();
            if (message == null || message.equalsIgnoreCase("exit")) {
                break;
            }
            databaseManager.insertMessage(currentChatId, user.getId(), message);
            sendMessageToChat(message);
        }
    }

    private void sendMessageToChat(String message){
        List<Integer> chatMembers = databaseManager.getChatMembers(currentChatId);
        for (Integer memberId: chatMembers){
            if (memberId.equals(user.getId())) continue;
            try {
                new PrintWriter(new Socket(clientSocket.getInetAddress(), clientSocket.getPort()).getOutputStream(), true)
                        .println(Constants.NEW_MESSAGE + Constants.MESSAGE_SEPARATOR + new Message(message, String.valueOf(java.time.LocalDateTime.now()), user.getUsername()).toString() + Constants.DATA_SEPARATOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("Connection with client closed: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

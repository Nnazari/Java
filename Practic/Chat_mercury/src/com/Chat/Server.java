package com.Chat;

import com.Chat.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private DataBaseManager databaseManager;

    public Server() {
        databaseManager = new DataBaseManager();
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            System.out.println("Server started on port " + Constants.SERVER_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, databaseManager);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        try {
            databaseManager.close();
            if (serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

    }
}

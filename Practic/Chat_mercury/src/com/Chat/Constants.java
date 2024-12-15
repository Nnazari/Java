package com.Chat;

public class Constants {
    public static final int SERVER_PORT = 5000;
    public static final String SERVER_HOST = "localhost";

    public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    public static final String AUTH_FAIL = "AUTH_FAIL";
    public static final String CHAT_LIST = "CHAT_LIST";
    public static final String CHAT_JOIN_SUCCESS = "CHAT_JOIN_SUCCESS";
    public static final String CHAT_JOIN_FAIL = "CHAT_JOIN_FAIL";
    public static final String NEW_MESSAGE = "NEW_MESSAGE";

    public static final String MESSAGE_SEPARATOR = "::";
    public static final String DATA_SEPARATOR = ";;";

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/chat_app";
    public static final String DB_USER = "user";
    public static final String DB_PASSWORD = "password";
}
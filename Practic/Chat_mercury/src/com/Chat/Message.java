package com.Chat;

public class Message {
    private String text;
    private String timestamp;

    private String username;
    public Message(String text, String timestamp, String username) {
        this.text = text;
        this.timestamp = timestamp;
        this.username = username;
    }


    public String getText() {
        return text;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public String getUsername(){
        return username;
    }


    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, username, text );
    }
}

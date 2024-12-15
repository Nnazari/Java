package com.Chat;

import com.Chat.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataBaseManager {
    private Connection connection;

    public DataBaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PASSWORD);
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws SQLException {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "    id SERIAL PRIMARY KEY," +
                "    username VARCHAR(255) NOT NULL UNIQUE," +
                "    password VARCHAR(255) NOT NULL" +
                ");";
        String chatsTable = "CREATE TABLE IF NOT EXISTS chats (" +
                "    id SERIAL PRIMARY KEY," +
                "    name VARCHAR(255) NOT NULL" +
                ");";
        String chatMembersTable = "CREATE TABLE IF NOT EXISTS chat_members (" +
                "    id SERIAL PRIMARY KEY," +
                "    chat_id INT NOT NULL," +
                "    user_id INT NOT NULL," +
                "    FOREIGN KEY (chat_id) REFERENCES chats(id)," +
                "    FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        String messagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                "    id SERIAL PRIMARY KEY," +
                "    chat_id INT NOT NULL," +
                "    user_id INT NOT NULL," +
                "    message TEXT NOT NULL," +
                "    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (chat_id) REFERENCES chats(id)," +
                "    FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        try(Statement statement = connection.createStatement()){
            statement.execute(usersTable);
            statement.execute(chatsTable);
            statement.execute(chatMembersTable);
            statement.execute(messagesTable);
        }

    }

    public User authenticate(String username, String password) {
        String query = "SELECT id, username FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                return new User(id, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Chat> getChats(int userId) {
        List<Chat> chats = new ArrayList<>();
        String query = "SELECT c.id, c.name " +
                "FROM chats c " +
                "INNER JOIN chat_members cm ON c.id = cm.chat_id " +
                "WHERE cm.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                chats.add(new Chat(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }


    public boolean isUserInChat(int userId, int chatId) {
        String query = "SELECT 1 FROM chat_members WHERE user_id = ? AND chat_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getChatMembers(int chatId) {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM chat_members WHERE chat_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userIds.add(resultSet.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }
    public void insertMessage(int chatId, int userId, String message) {
        String query = "INSERT INTO messages (chat_id, user_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, chatId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, message);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessages(int chatId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.message, m.timestamp, u.username FROM messages m " +
                "INNER JOIN users u ON m.user_id = u.id "+
                "WHERE m.chat_id = ? ORDER BY m.timestamp ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String username = resultSet.getString("username");
                messages.add(new Message(message, timestamp.toString(), username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }



    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


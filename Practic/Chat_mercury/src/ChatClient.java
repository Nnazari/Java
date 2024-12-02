// Клиентский класс
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера
    private static final int PORT = 8818; // Порт сервера

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT); // Создаем сокет для подключения к серверу
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Создаем поток ввода для чтения сообщений от сервера
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // Создаем поток вывода для отправки сообщений на сервер
             Scanner scanner = new Scanner(System.in)) { // Создаем сканер для чтения сообщений с консоли

            System.out.println("Подключен к серверу.");

            // Запускаем новый поток для обработки сообщений от сервера
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) { // Читаем сообщения из потока ввода
                        System.out.println(message); // Выводим сообщение на консоль
                    }
                } catch (IOException e) { // Обработка исключений ввода-вывода
                    System.err.println("Ошибка соединения: " + e.getMessage());
                }
            }).start();

            // Отправка сообщений на сервер
            while (true) { // Бесконечный цикл для отправки сообщений
                System.out.print("Введите сообщение: ");
                String message = scanner.nextLine();// Читаем сообщение с консоли
                if (message.equalsIgnoreCase("exit")) {
                    writer.println("exit");  // Отправляем сообщение серверу для выхода
                    break;
                }
                writer.println(message); // Отправляем сообщение на сервер
            }

        } catch (IOException e) { // Обработка исключений ввода-вывода
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}
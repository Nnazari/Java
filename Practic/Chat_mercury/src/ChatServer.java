// Серверный класс
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
public class ChatServer {

    private static final int PORT = 8818; // Порт для прослушивания соединений
    private static List<PrintWriter> clientWriters = new ArrayList<>(); // Список для хранения потоков вывода для каждого клиента

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Создаем серверный сокет на указанном порту
            System.out.println("Сервер запущен на порту " + PORT);


            while (true) { // Бесконечный цикл для ожидания новых клиентов
                Socket clientSocket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Новый клиент подключился: " + clientSocket.getInetAddress());

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true); // Создаем поток вывода для отправки сообщений клиенту
                clientWriters.add(writer); // Добавляем поток вывода в список

                // Запускаем новый поток для обработки каждого клиента
                new Thread(() -> handleClient(clientSocket, writer)).start();

            }
        } catch (IOException e) { // Обработка исключений ввода-вывода
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    // Метод для обработки сообщений от клиента
    private static void handleClient(Socket clientSocket, PrintWriter writer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) { // Создаем поток ввода для чтения сообщений от клиента
            String message;

            while ((message = reader.readLine()) != null) { // Читаем сообщения из потока ввода
                System.out.println("Сообщение от клиента:" + clientSocket.getInetAddress()+" "+message);
                broadcastMessage(message, writer); // Рассылаем сообщение всем клиентам
            }
        } catch (IOException e) { // Обработка исключений ввода-вывода
            System.err.println("Ошибка клиента: " + e.getMessage());
        } finally { // Закрываем сокет и удаляем поток вывода из списка
            clientWriters.remove(writer);
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    // Метод для рассылки сообщения всем клиентам
    private static void broadcastMessage(String message, PrintWriter sender) {
        for (PrintWriter writer : clientWriters) { // Проходим по списку потоков вывода
            if (writer != sender) { // Не отправляем сообщение отправителю
                writer.println(message); // Отправляем сообщение клиенту
            }
        }
    }
}

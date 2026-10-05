package main.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends Thread{
    private final Socket clientSocket;
    private static final String welcomeInfo = """
            Студент: Федосюк Олександр Олександрович
            Група: 4141
            Варіант: 18
            Завдання: Інвертувати всі слова в рядку""";

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String clientAddress = clientSocket.getRemoteSocketAddress().toString();
        System.out.println("Підключено нового клієнта: " + clientAddress);
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
            sendWelcome(out);    // Відправлення привітання клієнту

            String request;     // Повідомлення від клієнта
            while ((request = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(request.trim())) {  // Завершення роботи клієнтом
                    out.println("BYE");
                    break;
                }

                System.out.println("[" + clientAddress + "] >> " + request);
                String response = reverseWords(request);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Помилка зв'язку з клієнтом " + clientAddress + ": " + e.getMessage());
        } finally {
            closeSocket();
            System.out.println("Клієнт " + clientAddress + " відключився.\n");
        }
    }

    private String reverseWords(String str) {
        // Перетворюємо строку на масив слів
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            // Перевіряємо, чи є останній елемент слова літерою/цифрою
            int letterIndex = w.length() - 1;
            if (!Character.isLetterOrDigit(w.charAt(letterIndex))) {
                // Якщо останній елемент це не літера/цифра, то шукаємо першу появу літери/цифри
                while (letterIndex >= 0 && !Character.isLetterOrDigit(w.charAt(letterIndex - 1))) letterIndex--;
            } else letterIndex++;   // Інакше останній елемент вже є літерою/цифрою

            // Відділяємо слово від зайвих символів та перевертаємо його
            StringBuilder word = new StringBuilder(w.substring(0, letterIndex)).reverse();
            String punctuation = w.substring(letterIndex);
            // Склеюємо перевернуте слово та відділені символи
            sb.append(word).append(punctuation).append(" ");
        }
        return sb.toString();
    }

    // Метод відправлення "Вітальної інформації" клієнту
    private void sendWelcome(PrintWriter out) {
        String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        out.println(welcomeInfo + "\n[ЧАС СЕРВЕРА] " + currentTime + "\nEND_OF_WELCOME");
    }

    private void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException ignored) {}
    }
}

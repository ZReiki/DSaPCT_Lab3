package main.client;

import java.io.*;
import java.net.Socket;

public class ClientMain {
    private static final String HOST = "localhost";
    private static final int PORT = 4004;

    public static void main(String[] args) {
        new ClientMain().run();
    }

    private void run() {
        try (
                Socket clientSocket = new Socket(HOST, PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ){
            readWelcome(in);    // Вітальна інформація

            while (true) {
                System.out.print("\nВведіть ваш запит: ");
                String msg = reader.readLine();

                if (!"".equals(msg)) {
                    out.println(msg);

                    String response = in.readLine();
                    if (response == null || "BYE".equalsIgnoreCase(response)) {
                        System.out.println("З'єднання завершено.");
                        break;
                    }
                    System.out.println("[ВІДПОВІДЬ СЕРВЕРА] >> " + response);
                } else {
                    System.err.println("Запит не може бути порожнім! Будь ласка введіть будь-яке слово чи речення");
                }
            }

        } catch (IOException e) {
            System.err.println("Помилка клієнта: " + e.getMessage());
        }
    }

    // Отримання "Вітальної інформації" від Сервера
    private void readWelcome(BufferedReader in) throws IOException {
        String responseLine;
        while((responseLine = in.readLine()) != null) {
            if (responseLine.equals("END_OF_WELCOME")) {
                break;
            }
            else System.out.println(responseLine);
        }
    }
}

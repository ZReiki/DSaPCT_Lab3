package main.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 4004;

    public static void main(String[] args){
        new ServerMain().start();
    }

    private void start() {
        System.out.println("Сервер запущено на порту " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Помилка роботи сервера: " + e.getMessage());
        } finally {
            System.out.println("Сервер зупинено!");
        }
    }
}

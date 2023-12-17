package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен. Ожидание подключения клиента...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключен клиент: " + clientSocket.getInetAddress().getHostAddress());
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String input = in.readLine();
                int matrixSize = Integer.parseInt(input);
                int[][] matrix = generateMatrix(matrixSize);

                for (int i = 0; i < matrixSize; i++) {
                    StringBuilder rowBuilder = new StringBuilder();
                    for (int j = 0; j < matrixSize; j++) {
                        rowBuilder.append(matrix[i][j]).append(" ");
                    }
                    out.println(rowBuilder.toString().trim());
                }

                double mainDiagonalAvg = calculateMainDiagonalAvg(matrix);
                double secondaryDiagonalAvg = calculateSecondaryDiagonalAvg(matrix);
                double ratio = mainDiagonalAvg / secondaryDiagonalAvg;
                out.println("Ответ сервера: " + ratio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int[][] generateMatrix(int size) {
            int[][] matrix = new int[size][size];
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextInt(10);
                }
            }

            return matrix;
        }

        private double calculateMainDiagonalAvg(int[][] matrix) {
            double sum = 0;
            int size = matrix.length;

            for (int i = 0; i < size; i++) {
                sum += matrix[i][i];
            }

            return sum / size;
        }

        private double calculateSecondaryDiagonalAvg(int[][] matrix) {
            double sum = 0;
            int size = matrix.length;

            for (int i = 0; i < size; i++) {
                sum += matrix[i][size - 1 - i];
            }

            return sum / size;
        }
    }
}
package edu.escuelaing.arem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConcurrentWebServerTester {

    public static void main(String[] args) {
        int numberOfThreads = 100; // Número de hilos concurrentes
        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(new WebServerRequestTask());
            thread.start();
        }
    }

    private static class WebServerRequestTask implements Runnable {
        @Override
        public void run() {
            try {
                // Establecer la URL del servidor web local
                URL url = new URL("http://localhost:35000/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Leer la respuesta del servidor
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Mostrar la respuesta del servidor
                System.out.println("Response from server:\n" + response.toString());

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

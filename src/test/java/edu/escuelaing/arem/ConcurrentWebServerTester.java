package edu.escuelaing.arem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class tests a concurrent web server by spawning multiple threads,
 * each sending HTTP GET requests to the server.
 */
public class ConcurrentWebServerTester {

    /**
     * Main method to start testing the concurrent web server.
     * It spawns multiple threads to simulate concurrent requests.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        int numberOfThreads = 100; // Number of concurrent threads
        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(new WebServerRequestTask());
            thread.start();
        }
    }

    /**
     * Inner class representing a task that sends HTTP GET request to the web server.
     */
    private static class WebServerRequestTask implements Runnable {

        /**
         * Executes the task of sending an HTTP GET request to the web server.
         */
        @Override
        public void run() {
            try {
                // Set the URL of the local web server
                URL url = new URL("http://localhost:35000/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Read server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Display server response
                System.out.println("Response from server:\n" + response.toString());

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

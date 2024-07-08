package edu.escuelaing.arem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ConcurrentTestWebApp is a basic web application that uses sockets to handle HTTP connections.
 * It listens on a specified port (default 4567 or configurable via the PORT environment variable).
 * Responds with a simple HTML page when a connection is established.
 */
public class ConcurrentTestWebApp {
    private static final int DEFAULT_PORT = 4567;

    /**
     * Main method that starts the server.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an error occurs while trying to listen on the specified port.
     */
    public static void main(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = null;
        boolean running = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Ready to receive...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Error accepting connection.");
                System.exit(1);
            }
            // Open input and output streams to communicate with the client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            // Read the client request line by line
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                // If no more data to read, end reading
                if (!in.ready()) {
                    break;
                }
            }
            // Prepare HTTP response with a basic HTML page
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<title>Document Title</title>\n"
                    + "</head>"
                    + "<body>"
                    + "My Website"
                    + "</body>"
                    + "</html>";
            // Send response to the client
            out.println(outputLine);
            // Close streams and client socket
            out.close();
            in.close();
            clientSocket.close();
        }
        // Close the server socket when finished
        serverSocket.close();
    }

    /**
     * Gets the port on which the server should listen.
     *
     * @return The port configured via the PORT environment variable, or the default port (4567) if not configured.
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return DEFAULT_PORT; // return default port if PORT environment variable is not configured
    }
}

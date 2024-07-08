package edu.escuelaing.arem;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConcurrentHttpServer is a basic HTTP server that handles concurrent connections using sockets.
 * It listens on a specified port (default 35000 or configurable via PORT environment variable).
 * It responds with simple HTML pages when a connection is established.
 */
public class ConcurrentHttpServer {
    private static final Map<String, String> sessions = new HashMap<>();
    private static final String SESSION_COOKIE_NAME = "session-id";
    private static final int MAX_THREADS = 10; // Maximum number of threads in the thread pool

    /**
     * Main method that starts the server.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If an error occurs while trying to listen on the specified port.
     */
    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS); // Thread pool

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000); // Server port
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        System.out.println("Server ready to receive connections on port 35000 ...");

        while (true) {
            final Socket clientSocket = serverSocket.accept();
            System.out.println("Incoming connection from: " + clientSocket.getInetAddress());

            pool.execute(() -> handleClientRequest(clientSocket)); // Execute request in thread from pool
        }
    }

    /**
     * Handles client requests.
     *
     * @param clientSocket The client socket to handle the request.
     */
    private static void handleClientRequest(Socket clientSocket) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            StringBuilder request = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                request.append(inputLine).append("\r\n");

                if (inputLine.isEmpty()) {
                    break;
                }
            }

            String sessionId = getSessionIdFromRequest(request.toString());
            String response;

            if (request.toString().contains("POST /upload HTTP/1.1")) {
                response = processFileUpload(request.toString(), sessionId);
            } else {
                response = processRequest(sessionId);
            }

            out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the session ID from the request headers.
     *
     * @param request The HTTP request headers.
     * @return The session ID extracted from the request headers.
     */
    private static String getSessionIdFromRequest(String request) {
        String sessionId = null;
        String[] lines = request.split("\r\n");
        for (String line : lines) {
            if (line.startsWith("Cookie:")) {
                String cookieLine = line.substring("Cookie:".length()).trim();
                String[] cookies = cookieLine.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.split("=");
                    if (parts.length == 2 && parts[0].trim().equals(SESSION_COOKIE_NAME)) {
                        sessionId = parts[1].trim();
                    }
                }
            }
        }
        return sessionId;
    }

    /**
     * Processes the HTTP request and prepares the response for the main page.
     *
     * @param sessionId The session ID of the current user.
     * @return The HTTP response containing the main page content.
     */
    private static String processRequest(String sessionId) {
        if (sessionId == null || !sessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            sessions.put(sessionId, "User_" + sessions.size());
        }

        String content = "<html><head><title>Session Example</title></head><body>"
                + "<h1>Session Example</h1>"
                + "<p>Welcome, " + sessions.get(sessionId) + "!</p>"
                + "<form action=\"/upload\" method=\"post\" enctype=\"multipart/form-data\">"
                + "<input type=\"file\" name=\"file\" accept=\".txt,.pdf,image/*\">"
                + "<br><br>"
                + "<input type=\"submit\" value=\"Upload File\">"
                + "</form>"
                + "</body></html>";

        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "Set-Cookie: " + SESSION_COOKIE_NAME + "=" + sessionId + "; HttpOnly; Path=/\r\n"
                + "\r\n"
                + content;

        return response;
    }

    /**
     * Processes the HTTP request for file upload and prepares the response.
     *
     * @param request   The HTTP request headers and body.
     * @param sessionId The session ID of the current user.
     * @return The HTTP response confirming the file upload and displaying its content.
     */
    private static String processFileUpload(String request, String sessionId) {
        // Extract file content from request body
        int start = request.indexOf("\r\n\r\n") + 4; // beginning of file content
        String fileContent = request.substring(start);

        // Generate unique file name
        String fileId = UUID.randomUUID().toString();
        String fileName = fileId + ".txt"; // example: generate a text file

        // Store file content somewhere (e.g., in memory or file system)
        saveFile(fileName, fileContent);

        // Prepare response
        String content = "<html><head><title>File Uploaded Successfully</title></head><body>"
                + "<h1>File Uploaded Successfully!</h1>"
                + "<p>File name: " + fileName + "</p>"
                + "<p>File content:</p>"
                + "<pre>" + fileContent + "</pre>"
                + "<p><a href=\"/\">Back to Home</a></p>"
                + "</body></html>";

        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "Set-Cookie: " + SESSION_COOKIE_NAME + "=" + sessionId + "; HttpOnly; Path=/\r\n"
                + "\r\n"
                + content;

        return response;
    }

    /**
     * Saves the file content to storage (e.g., file system).
     *
     * @param fileName    The name of the file.
     * @param fileContent The content of the file.
     */
    private static void saveFile(String fileName, String fileContent) {
        // Implement file saving logic (e.g., save to file system, database, etc.)
        // For demonstration, printing to console
        System.out.println("File Uploaded Successfully!");
        System.out.println("File name: " + fileName);
        System.out.println("File content:\n" + fileContent);
    }
}

# Concurrent HTTP Server

ConcurrentHttpServer is a Java application that implements a basic HTTP server capable of handling concurrent connections using sockets. It responds with simple HTML pages and supports file uploads with session management.

## Features

- **Concurrent Handling**: Uses a thread pool to manage multiple concurrent connections.
- **Session Management**: Maintains user sessions with session IDs stored in cookies.
- **File Upload**: Supports file uploads with basic storage functionality.
- **Simple HTTP Responses**: Responds with HTML content for client requests.

## Project Structure

### Classes

1. **ConcurrentHttpServer.java**:
   - Main class that initializes the HTTP server.
   - Uses a thread pool (`ExecutorService`) to handle concurrent client connections.
   - Listens on port 35000 by default.

2. **handleClientRequest(Socket clientSocket)**:
   - Method to handle incoming client requests.
   - Reads HTTP requests, manages sessions using session cookies, and processes GET and POST requests.
   - Implements basic file upload functionality.

3. **getSessionIdFromRequest(String request)**:
   - Utility method to extract session ID from HTTP request headers.
   - Parses cookies and retrieves the session ID for session management.

4. **processRequest(String sessionId)**:
   - Method to process HTTP GET requests.
   - Generates HTML responses based on session information.
   - Includes a form for file uploads.

5. **processFileUpload(String request, String sessionId)**:
   - Method to process HTTP POST requests for file uploads.
   - Saves uploaded files and generates HTML responses confirming the upload.

## Usage

### Running the Server

To compile and run the ConcurrentHttpServer, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/JohannBulls/SparkWebApp
   cd SparkWebApp
   ```

2. Compile the Java files using Maven:
   ```bash
   mvn clean install
   ```

3. Run the server using Java:
   ```bash
   java -jar target/ConcurrentHttpServer-1.0-SNAPSHOT.jar
   ```

   By default, the server will listen on port 35000. You can configure the port using the `PORT` environment variable.

### Testing

You can test the server using the `ConcurrentWebServerTester` class, which simulates multiple concurrent clients sending HTTP GET requests.

1. Compile the tester class:
   ```bash
   javac ConcurrentWebServerTester.java
   ```

2. Run the tester:
   ```bash
   java ConcurrentWebServerTester
   ```

   This will simulate multiple clients sending GET requests to `http://localhost:35000/`.

### Example Usage

- Access the server via a web browser or using tools like `curl`.
- Upload files using the provided file upload form.
- See responses and session management in action.

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests with improvements or additional features.

## Author

- Johann Amaya Lopez - [GitHub](https://github.com/JohannBulls/SparkWebApp)

## License

This project is licensed under the MIT License. See the LICENSE file for details.

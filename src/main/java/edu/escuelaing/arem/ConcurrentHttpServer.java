package edu.escuelaing.arem;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConcurrentHttpServer es un servidor HTTP concurrente que gestiona sesiones de usuario utilizando cookies.
 * Permite manejar múltiples conexiones simultáneas y asigna un ID de sesión único a cada usuario que se conecta.
 */
public class ConcurrentHttpServer {
    //sessions: Un mapa que mantiene el estado de las sesiones de los usuarios, utilizando el ID de sesión como clave y el nombre de usuario como valor.
    //SESSION_COOKIE_NAME: Nombre de la cookie de sesión que se utiliza para identificar y mantener el estado de la sesión del usuario en las solicitudes.
    private static final Map<String, String> sessions = new HashMap<>();
    private static final String SESSION_COOKIE_NAME = "session-id";

    /**
     * Método principal que inicia el servidor.
     *
     * @param args Argumentos de línea de comandos (no se utilizan).
     * @throws IOException Si hay un error al intentar escuchar en el puerto 35000.
     */
    public static void main(String[] args) throws IOException {
        //Crea un pool de hilos (ExecutorService) con 10 threads para manejar conexiones concurrentes.
        //Crea un ServerSocket que escucha en el puerto 35000.
        //Dentro de un bucle infinito, acepta conexiones entrantes (clientSocket) y las envía al pool de hilos para manejarlas concurrentemente usando pool.execute.

        ExecutorService pool = Executors.newFixedThreadPool(1); // Pool de hilos con 10 threads

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("No se pudo escuchar en el puerto: 35000.");
            System.exit(1);
        }

        System.out.println("Servidor listo para recibir conexiones en el puerto 35000 ...");

        while (true) {
            final Socket clientSocket = serverSocket.accept();
            System.out.println("Conexión entrante desde: " + clientSocket.getInetAddress());

            // Utilizamos el pool de hilos para manejar las conexiones concurrentemente
            pool.execute(() -> handleClientRequest(clientSocket));
        }
    }

    /**
     * Maneja la solicitud de un cliente.
     *
     * @param clientSocket Socket del cliente que realiza la solicitud.
     */
    private static void handleClientRequest(Socket clientSocket) {
        //handleClientRequest
        //Gestiona la solicitud de un cliente en un hilo separado.
        //Lee la solicitud HTTP del cliente y la almacena en request.
        //Llama a getSessionIdFromRequest para obtener el ID de sesión de las cookies de la solicitud.
        //Llama a processRequest para generar una respuesta basada en el ID de sesión.
        //Envía la respuesta al cliente y cierra el socket.

        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            StringBuilder request = new StringBuilder();

            // Lee la solicitud del cliente línea por línea
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibido: " + inputLine);
                request.append(inputLine).append("\r\n");

                // Si la línea está vacía, significa el final de la solicitud HTTP
                if (inputLine.isEmpty()) {
                    break;
                }
            }

            // Procesa la solicitud para obtener el ID de sesión del cliente
            String sessionId = getSessionIdFromRequest(request.toString());

            // Procesa la solicitud y genera la respuesta basada en el ID de sesión
            String response = processRequest(sessionId);

            // Envía la respuesta al cliente
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
     * Obtiene el ID de sesión desde la solicitud HTTP del cliente.
     *
     * @param request Solicitud HTTP del cliente.
     * @return ID de sesión si está presente en las cookies, o null si no se encuentra.
     */
    private static String getSessionIdFromRequest(String request) {
        //processRequest
        //Verifica si el sessionId recibido es válido.
        //Si no es válido (o no existe), crea un nuevo ID de sesión utilizando UUID.randomUUID().
        //Asigna un nombre de usuario simulado (User_X) al nuevo ID de sesión y lo guarda en sessions.
        //Construye y devuelve una respuesta HTTP con el contenido HTML que incluye un mensaje de bienvenida y un formulario de cierre de sesión.
        //Configura una cookie de sesión en la respuesta utilizando Set-Cookie.
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
     * Procesa la solicitud del cliente y genera la respuesta.
     *
     * @param sessionId ID de sesión del cliente.
     * @return Respuesta HTTP con el contenido HTML apropiado.
     */
    private static String processRequest(String sessionId) {
        // Verifica si hay una sesión válida para el ID de sesión dado
        if (sessionId == null || !sessions.containsKey(sessionId)) {
            // Crea un nuevo ID de sesión y asigna un nombre de usuario genérico
            sessionId = UUID.randomUUID().toString();
            sessions.put(sessionId, "Usuario_" + sessions.size());
        }

        // Prepara el contenido HTML de la respuesta
        String content = "<html><head><title>Ejemplo de Sesión</title></head><body>"
                + "<h1>Ejemplo de Sesión</h1>"
                + "<p>Bienvenido, " + sessions.get(sessionId) + "!</p>"
                + "<form action=\"/logout\" method=\"post\">"
                + "<input type=\"submit\" value=\"Cerrar sesión\">"
                + "</form>"
                + "</body></html>";

        // Configura la respuesta HTTP con el contenido y la cookie de sesión
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "Set-Cookie: " + SESSION_COOKIE_NAME + "=" + sessionId + "; HttpOnly; Path=/\r\n"
                + "\r\n"
                + content;

        return response;
    }
}

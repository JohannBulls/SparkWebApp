package edu.escuelaing.arem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ConcurrentTestWebApp es una aplicación web básica que utiliza sockets para manejar conexiones HTTP.
 * Escucha en un puerto especificado (predeterminado 4567 o configurable mediante la variable de entorno PORT).
 * Responde con una página HTML simple cuando se establece una conexión.
 */
public class ConcurrentTestWebApp {
    private static final int DEFAULT_PORT = 4567;

    /**
     * Método principal que inicia el servidor.
     *
     * @param args Argumentos de línea de comandos (no se utilizan).
     * @throws IOException Si hay un error al intentar escuchar en el puerto especificado.
     */
    public static void main(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = null;
        boolean running = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado en el puerto: " + port);
        } catch (IOException e) {
            System.err.println("No se pudo escuchar en el puerto: " + port);
            System.exit(1);
        }
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Error al aceptar la conexión.");
                System.exit(1);
            }
            // Abre flujos de entrada y salida para comunicarse con el cliente
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            // Lee la solicitud del cliente línea por línea
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibido: " + inputLine);
                // Si no hay más datos para leer, termina la lectura
                if (!in.ready()) {
                    break;
                }
            }
            // Prepara la respuesta HTTP con una página HTML básica
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<title>Título del documento</title>\n"
                    + "</head>"
                    + "<body>"
                    + "Mi Sitio Web"
                    + "</body>"
                    + "</html>";
            // Envía la respuesta al cliente
            out.println(outputLine);
            // Cierra los flujos y el socket del cliente
            out.close();
            in.close();
            clientSocket.close();
        }
        // Cierra el socket del servidor al finalizar
        serverSocket.close();
    }

    /**
     * Obtiene el puerto en el que el servidor debe escuchar.
     *
     * @return El puerto configurado mediante la variable de entorno PORT, o el puerto predeterminado (4567) si no está configurado.
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return DEFAULT_PORT; // devuelve el puerto predeterminado si la variable de entorno "PORT" no está configurada
    }
}

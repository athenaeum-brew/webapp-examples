import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(12345);

            System.out.println("\nServer waiting for client on port 12345");

            // Accept client connection
            Socket socket = serverSocket.accept();
            System.out.println("\nClient connected: " + socket);

            // Create input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Read and print client's message
            String message = in.readLine();
            System.out.println("\nMessage from Client: " + message);

            // Send response to client
            out.println("Hello from Java Server");

            System.out.println("");
            // Close streams and socket
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

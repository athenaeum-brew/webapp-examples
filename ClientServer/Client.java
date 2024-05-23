import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 12345);
            System.out.println("\nConnected to server: " + socket);

            // Create input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send message to server
            out.println("Hello from Client");

            // Read and print server's response
            String response = in.readLine();
            System.out.println("\nResponse from server: \u001B[32m" + response + "\u001B[0m");

            // Close streams and socket
            in.close();
            out.close();
            socket.close();

            System.out.println("\nConnection closed\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

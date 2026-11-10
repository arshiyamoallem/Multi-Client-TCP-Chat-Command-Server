import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    static int port = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port + ". Waiting for clients to connect...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A client has connected: " + clientSocket.getRemoteSocketAddress());

                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
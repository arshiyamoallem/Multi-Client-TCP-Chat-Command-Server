import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static int port = 8080;
    public static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static synchronized boolean addClient(String username, ClientHandler clientHandler) {
        for (ClientHandler client : clients) {
            if (client.getUsername() != null && client.getUsername().equalsIgnoreCase(username)) {
                return false; // Username already taken
            }
        }
        clients.add(clientHandler);
        return true; // Username is valid and not taken
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

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
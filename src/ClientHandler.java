import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                String msg = in.readLine();

                if (msg == null || msg.equals("exit")) {
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                    break;
                }
                System.out.println("Received from " + socket.getRemoteSocketAddress() + ": " + msg);
                out.println("Server echo:" + msg);
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
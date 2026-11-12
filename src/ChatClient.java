import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.String;
import java.lang.Thread;

public class ChatClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the chat server.");
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        if (serverMessage.trim().startsWith("Enter your username")) {
                            System.out.print(serverMessage.trim() + " ");
                            System.out.flush();
                        } else {
                            System.out.println(serverMessage);
                        }
                    }
                    System.out.println("Disconnected from server.");
                    System.exit(0);

                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                    System.exit(0);
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

            while (scanner.hasNextLine()) {
                String userMessage = scanner.nextLine();
                out.println(userMessage);
                if (userMessage.equals("/exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}

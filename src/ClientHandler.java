import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void promptUsername(BufferedReader in) throws IOException {
        while (true) {
            out.println("Enter your username: ");
            String chosenName = in.readLine();

            if (chosenName == null) {
                throw new IOException("Client disconnected before providing a username.");
            }

            chosenName = chosenName.trim();

            if (chosenName.isEmpty() || chosenName.contains(" ")) {
                out.println("Invalid username (cannot be empty or contain spaces). Try again.");
                continue;
            }

            this.username = chosenName;

            if (ChatServer.addClient(chosenName, this)) {
                out.println(
                        "Welcome, " + this.username
                                + "! You can start chatting now. Type /help for a list of commands.");
                break;
            } else {
                this.username = null; // Reset username if it's invalid or taken
                out.println("Username '" + chosenName + "' is invalid or already taken. Try again.");
            }
        }

    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void handleCommand(String command) {
        String[] parts = command.trim().split(" ", 3);
        String action = parts[0].toLowerCase();

        switch (action) {
            case "/help":
                sendHelp();
                break;
            case "/list":
                sendUserList();
                break;
            // for chat
            case "/w":
                handleWhisper(parts);
                break;
            default:
                sendMessage("Unknown command. Type /help for a list of commands.");
                break;
        }
    }

    public void sendHelp() {
        sendMessage("Available commands: \n" +
                "/exit - Exit the chat\n" +
                "/help - Show this help message\n" +
                "/list - List all connected users\n" +
                "/w <username> <message> - Send a private message to a user");
    }

    public void sendUserList() {
        StringBuilder userList = new StringBuilder("Online users: ");
        for (ClientHandler client : ChatServer.clients) {
            if (client.getUsername() != null) {
                userList.append(client.getUsername()).append(" ");
            }
        }
        sendMessage(userList.toString().trim());
    }

    public void handleWhisper(String[] parts) {
        if (parts.length < 3 || parts[2].trim().isEmpty()) {
            sendMessage("Usage: /w <username> <message>");
            return;
        }

        String targetUser = parts[1];
        String privateMessage = parts[2];

        if (targetUser.equalsIgnoreCase(this.username)) {
            sendMessage("You cannot send a private message to yourself.");
            return;
        }

        for (ClientHandler client : ChatServer.clients) {
            if (client.getUsername() != null && client.getUsername().equalsIgnoreCase(targetUser)) {
                client.sendMessage("[Whisper from " + this.username + "]: " + privateMessage);
                sendMessage("[Whisper to " + targetUser + "]: " + privateMessage);
                return;
            }
        }
        sendMessage("User '" + targetUser + "' not found.");
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            promptUsername(this.in);
            ChatServer.broadcast(this.username + " has joined the chat!", this);

            while (true) {
                String msg = in.readLine();

                if (msg == null || msg.equals("/exit")) {
                    System.out.println("Client disconnected: " + this.username);
                    break;
                }

                if (msg.startsWith("/")) {
                    handleCommand(msg);
                } else {
                    System.out.println("Received from " + this.username + ": " + msg);
                    ChatServer.broadcast("[" + this.username + "]: " + msg, this);
                }
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            ChatServer.removeClient(this);
            if (this.username != null) {
                ChatServer.broadcast(this.username + " has left the chat.", this);
            }

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}

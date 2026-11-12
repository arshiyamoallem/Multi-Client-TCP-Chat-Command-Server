# Multi-Threaded TCP Chat & Command Server

A lightweight multi-client chat server built with pure Java. Users connect over TCP sockets, pick a username, exchange real-time messages, and run in-chat commands.

---

## Features

- **Concurrent chat:** Multiple terminal clients can connect and chat at the same time.
- **One thread per client:** Each connection runs on its own worker thread (`Runnable`), so one slow client doesn't block the others.
- **Thread-safe state:** Connected users are tracked in a `CopyOnWriteArrayList`, and username registration is synchronized so two clients can't claim the same name.
- **Usernames:** Unique (case-insensitive), no spaces, re-prompted until valid.
- **Join/leave announcements:** Everyone is notified when a user joins or leaves.
- **Commands:**
  - `/help` - List available commands
  - `/list` - Show all connected users
  - `/w <user> <message>` - Send a private message to one user
  - `/exit` - Disconnect cleanly
- **Safe teardown:** Sudden disconnects (e.g. closing the terminal) are handled in a `finally` block that removes the user, notifies the room, and closes the socket.

---

## Project Structure

```text
.
├── src/
│   ├── ChatServer.java      # Opens port 8080, accepts clients, tracks users, broadcasts messages
│   ├── ClientHandler.java   # Per-client worker thread: username prompt, message loop, commands
│   └── ChatClient.java      # Terminal client: sends keyboard input, prints incoming messages
├── Makefile                 # build, server, client and clean targets
├── run-server.bat           # Windows alternative to `make server`
├── run-client.bat           # Windows alternative to `make client`
└── .gitignore
```

---

## Requirements

- JDK 11 or newer (`javac -version` should work)
- `make` (optional, Windows users can use the `.bat` scripts instead)

---

## How to Run

### With make (Linux, macOS, WSL)

```bash
make server    # compile and start the server on port 8080
make client    # compile and start a client (run in each extra terminal)
make clean     # delete compiled files
```

### On Windows (no make)

```bat
run-server.bat
run-client.bat
```

### Manually

```bash
javac -d bin src/*.java
java -cp bin ChatServer
java -cp bin ChatClient
```

You can also connect with Netcat or Telnet instead of the Java client:

```bash
nc localhost 8080
```

---

## Example Session

```text
Enter your username: alice
Welcome, alice! You can start chatting now. Type /help for a list of commands.
bob has joined the chat!
[bob]: hey
/w bob meet at 5
[Whisper to bob]: meet at 5
/list
Online users: alice bob
```

---

## Key Concepts

- **Java sockets (`ServerSocket`, `Socket`):** Reliable TCP communication between separate processes.
- **Concurrency (`Thread`, `Runnable`):** Thread-per-client handling over blocking I/O.
- **Thread safety:** `CopyOnWriteArrayList` for safe iteration while clients join and leave, plus a `synchronized` method for username registration.
- **Resource management:** `try`/`catch`/`finally` to release sockets and remove disconnected users.
- **Message routing:** Broadcast to all other clients vs. direct delivery to a single client's socket for whispers.

---

## Known Limitations

- Messages are plain text and unencrypted.
- The client is hardcoded to `localhost:8080`.
- One thread per client doesn't scale to thousands of connections.

---

## Roadmap

- [ ] Configurable host and port
- [ ] Thread pool instead of a raw thread per client
- [ ] Chat rooms
- [ ] Binary, length-prefixed message protocol

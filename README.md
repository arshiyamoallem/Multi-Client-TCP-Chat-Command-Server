# Multi-Threaded TCP Chat & Command Server

A lightweight, multi-client chat and command server built with pure Java. It allows multiple users to connect simultaneously over TCP sockets, exchange real-time messages, and execute in-room commands without freezing or blocking.

---

## What It Does

* **Concurrent Chat:** Multiple terminal clients can connect and chat at the same time.
* **Non-Blocking I/O:** Each user runs on a separate worker thread (`Runnable`), so one user's lag doesn't slow down the room.
* **Thread-Safe State:** Uses `ConcurrentHashMap` to track connected users without race conditions or memory corruption.
* **Custom Command Protocol:**
  * `/list` — View all currently connected users.
  * `/w <user> <msg>` — Send a private direct message to a specific user.
  * `/help` — List available commands.
  * `/quit` — Disconnect cleanly from the server.
* **Safe Teardown:** Handles sudden disconnects (e.g., closing the terminal) without crashing the server.

---

## Project Structure

```text
src/
├── ChatServer.java      # Main server: opens port 8080, accepts clients, handles broadcast
├── ClientHandler.java   # Worker thread: reads messages and executes commands per user
└── ChatClient.java      # Client app: reads keyboard input and prints incoming messages
```

---

## How to Run

### 1. Compile

```bash
javac -d bin src/*.java
```

### 2. Start the Server

```bash
java -cp bin ChatServer
```
*(Runs on port 8080 by default)*

### 3. Connect Clients (Open multiple terminal windows)

```bash
java -cp bin ChatClient
```

*Or connect directly using Netcat / Telnet:*
```bash
nc localhost 8080
```

---

## Key Tech Concepts

* **Java Sockets (`ServerSocket`, `Socket`):** Reliable TCP streaming between independent processes.
* **Concurrency (`Thread`, `Runnable`):** Parallel connection handling to prevent blocking I/O.
* **Thread Safety (`ConcurrentHashMap`):** Safe concurrent reads and writes across multiple client threads.
* **Clean Resource Management:** `try-catch-finally` blocks to release OS sockets and remove inactive users.

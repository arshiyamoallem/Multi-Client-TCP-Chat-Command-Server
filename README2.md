# Authoritative Multi-Client TCP State Engine

A high-performance, concurrent, authoritative TCP server architecture designed as a 4th-year Software Engineering capstone or flagship personal project. Moving beyond simple text broadcasting, this framework establishes a stateful, persistent multiplayer environment—multiplexing real-time movement synchronization, concurrent world-state arrays, and a contextual social/chat routing layer under a unified network protocol layer.

---

## 1. System Architecture Blueprint

The server operates as an **Authoritative State Broker**. Sockets stream raw bytes natively across TCP; the engine decodes incoming packets asynchronously, synchronizes changes to a global matrix, and broadcasts frame deltas on a deterministic game loop interval.

```
       +------------------ Java TCP Command Server ------------------+
       |                                                             |
       |  [Connection Pool] --> [Command Router] --> [World State]    |
       |         |                     |                 |           |
       |         v                     v                 v           |
       |   Thread Handles        Parses `/move`     Thread-Safe Map  |
       |   & Keep-Alives         or `/chat`         X, Y, Z & Rooms  |
       |                                                             |
       +-------------------------------------------------------------+
               ^                             ^
               | (TCP Byte Stream)           | (TCP Byte Stream)
               v                             v
       +-----------------+           +-----------------+
       |   C CLI Client  |           |   C CLI Client  |
       |  (or Graphical) |           |  (or Graphical) |
       +-----------------+           +-----------------+
```

---

## 2. Core Architectural Pillars

### Ⅰ. Language Interoperability (Java Backend ↔ C Client)

- **Engine Layer:** Java (`java.net.ServerSocket`) manages high-level asynchronous worker thread allocation, object-oriented routing modules, and atomic synchronization matrices.
- **Interface Layer:** Existing C codebases interface directly with raw kernel sockets (`sys/socket.h`). Byte streams remain completely language-agnostic; structured packets map identically in memory across runtime environments.

### Ⅱ. Thread-Safe State Concurrency

- Implements synchronized state boundaries using non-blocking abstractions (`ConcurrentHashMap`, `AtomicInteger`) to bypass explicit coarse-grained locking bottlenecks.
- Isolates race conditions where user state calculations read from the matrix simultaneously while asynchronous network workers update adjacent memory partitions.

### Ⅲ. Proactive Fixed Ticking Loop

- Transitioned from standard reactive I/O (executing tasks solely on demand) to a proactive orchestration routine.
- A background execution scheduler triggers a loop precisely every **50ms (20 Ticks/Second)**. This worker pulls a baseline snapshot of modified memory coordinates and streams delta configurations downstream to connected endpoints.

### Ⅳ. Custom Framing & Multi-Protocol Partitioning

- TCP preserves byte streams but ignores application packet borders. This project implements a explicit application-layer frame syntax:
  - `[Byte 0]`: Message Context Type Flags (e.g., `0x01` = Physics Sync, `0x02` = Gameplay Command, `0x03` = Chat Message).
  - `[Byte 1-2]`: Big-Endian Payload Size Prefixes.
  - `[Byte 3+]`: Structured Variable Arrays or Raw ASCII Character Streams.

---

## 3. Core Software Engineering Concepts Mastery

Building this ecosystem directly highlights technical proficiencies that differentiate junior developers from systems-level engineers:

- **Low-Level Socket Multiplexing:** Moving past standard REST/HTTP frameworks to manage raw transport layers, network payloads, stream serialization, and OS kernel buffers.
- **Shared Memory Thread Contention:** Resolving structural data conflicts across hundreds of active concurrent handlers running simultaneously.
- **Network Latency & Scaling Optimization:** Managing small length-prefixed packet formats instead of heavy json objects to save bandwidth overhead.

---

## 4. Technical Roadmap & Project Progress

- [x] Initial conceptual architecture mapping
- [ ] Implement Java Concurrent Backbone & Thread Pool Scheduler
- [ ] Define shared Binary Header and Custom Packaging Specification
- [ ] Integrate C Terminal Interface with Network Socket Handlers
- [ ] Test synchronized multithreaded grid updates under simulated congestion

SRC := $(wildcard src/*.java)

.PHONY: build server client clean

build:
	mkdir -p bin
	javac -d bin $(SRC)

server: build
	java -cp bin ChatServer

client: build
	java -cp bin ChatClient

clean:
	rm -rf bin
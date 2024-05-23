#!/bin/bash

HOST="localhost"
PORT=12345

# Connect to the server and send a message
echo -e "Hello from Bash" | nc $HOST $PORT

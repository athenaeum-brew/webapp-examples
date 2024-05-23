import socket

def main():
    host = "localhost"
    port = 12345
    
    # Connect to the server
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((host, port))
    print("\nConnected to server", client_socket)

    # Send message to server
    message = "Hello from Python client"
    client_socket.sendall(message.encode())
    client_socket.shutdown(socket.SHUT_WR)  # Signal the end of the message

    # Receive response from server
    response = client_socket.recv(1024).decode()
    print("\nResponse from server: \u001B[32m", response, "\u001B[0m")

    # Close the socket
    client_socket.close()

    print("Connection closed\n")
if __name__ == "__main__":
    main()

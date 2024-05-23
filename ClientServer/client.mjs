import net from 'net';

const host = 'localhost';
const port = 12345;

const client = new net.Socket();

client.connect(port, host, () => {
    console.log('\nConnected to server');
    
    // Send message to server
    client.write('Hello from Node.js client');

    // Close the connection after sending the message
    client.end();
});

client.on('data', (data) => {
    console.log('\nResponse from server:', '\u001B[32m', data.toString(), '\u001B[0m');
});

client.on('close', () => {
    console.log('Connection closed\n');
});

export default client;

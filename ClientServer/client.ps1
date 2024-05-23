# Define server and port
$server = "localhost"
$port = 12345

# Create a TCP client
$client = New-Object System.Net.Sockets.TcpClient($server, $port)

# Get the network stream
$stream = $client.GetStream()

# Create a stream writer and reader
$writer = New-Object System.IO.StreamWriter($stream)
$reader = New-Object System.IO.StreamReader($stream)

# Send a message to the server
$message = "Hello from PowerShell"
$writer.WriteLine($message)
$writer.Flush()

# Read the response from the server
$response = $reader.ReadLine()
Write-Host "Server: $response"

# Close the connections
$writer.Close()
$reader.Close()
$stream.Close()
$client.Close()


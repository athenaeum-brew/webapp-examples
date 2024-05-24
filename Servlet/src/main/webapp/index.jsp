<!DOCTYPE html>
<html lang="en">
    <!-- 
        cd webapp
        python3 -m http.server 987
    -->
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Questioneer - v0.1</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@latest/dist/css/bootstrap.min.css">
</head>
<body>
    <main class="container">
        <h1><%= header %></h1>
        <pre>mvn clean package appengine:deploy</pre>
    </main>
</body>
</html>
<%!
    String header = "Hello, Servlet World!";
%>
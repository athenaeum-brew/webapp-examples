import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/*
 * mvn clean compile exec:java -Dexec.mainClass="UndertowMinimalServer"
 */

public class UndertowMinimalServer {
    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new SimpleHandler())
                .build();
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class SimpleHandler implements HttpHandler {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            // Thread.sleep(10_000);
            exchange.getResponseHeaders().put(io.undertow.util.Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello, Undertow!");
        }
    }
}

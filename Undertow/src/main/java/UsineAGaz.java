import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplate;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/*
 * mvn clean compile exec:java -Dexec.mainClass="UsineAGaz"
 */
public class UsineAGaz {

    private static final HttpString THREADNAME = new HttpString("Thread-Name");
    private static final HttpString ORDER = new HttpString("Order");
    private static final HttpString SERVERINSTANCE = new HttpString("Server-Instance");

    private final static Responder responder;
    private final static String identity;

    static {
        responder = new ResponderBlockingQueue();
        identity = Integer.toHexString(System.identityHashCode(UndertowThymeleafServer.class));
    }

    public static void main(String[] args) {

        // Create a RoutingHandler to handle different paths
        RoutingHandler routingHandler = new RoutingHandler();

        // Add your ThymeleafHandler
        routingHandler.get("/", new ThymeleafHandler());
        routingHandler.get("/data/{path}", new DataHandler());

        // Wrap the routingHandler with a CORS handler
        HttpHandler corsHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                // Add CORS headers to every response
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Origin"), "*");
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Methods"),
                        "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Credentials"), "true");
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Headers"), "*");
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Expose-Headers"), "*");
                exchange.getResponseHeaders().add(new HttpString("Access-Control-Max-Age"), "3600");
                // Delegate the request to the next handler in the chain (routingHandler)
                routingHandler.handleRequest(exchange);
            }
        };

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(corsHandler)
                .build();
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class DataHandler implements HttpHandler {

        PathTemplate template;

        public DataHandler() {
            template = PathTemplate.create("/data/{path}");
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            handleDataRequest(exchange);
        }

        private void handleDataRequest(HttpServerExchange exchange) {
            StringIntPair sip = responder.next();
            System.out.println(sip);

            // Add custom headers
            exchange.getResponseHeaders().put(THREADNAME, Thread.currentThread().getName());
            exchange.getResponseHeaders().put(SERVERINSTANCE, identity);
            exchange.getResponseHeaders().put(ORDER, sip.order);

            // Set the response content type and send the response data
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send(sip.word);

        }
    }

    static class ThymeleafHandler implements HttpHandler {

        private final TemplateEngine templateEngine;

        public ThymeleafHandler() {
            templateEngine = new TemplateEngine();
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setPrefix("/templates/");
            templateResolver.setSuffix(".html");
            templateEngine.setTemplateResolver(templateResolver);
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            renderTemplate(exchange, "usineAGaz"); // assuming index.html is your template file
        }

        private void renderTemplate(HttpServerExchange exchange, String templateName) {
            // Create a Thymeleaf context and add variables
            Context context = new Context();
            context.setVariable("title", responder.getClass().getSimpleName());
            context.setVariable("immutableList", Responder.immutableList);

            // Process the Thymeleaf template with the given context
            String html = templateEngine.process(templateName, context);

            // Set the response content type and send the rendered HTML as the response
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html;charset=UTF-8");
            exchange.getResponseSender().send(html);
        }

    }

    public record StringIntPair(String word, int order) {
    }

    public static interface Responder {
        final List<String> immutableList = List.of("Now,", "he", "has", "two", "problems.");

        StringIntPair next();
    }

    static class ResponderBlockingQueue implements Responder {
        private final BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(immutableList.size());

        ResponderBlockingQueue() {
            refillIfEmpty();
        }

        private synchronized void refillIfEmpty() {
            if (responseQueue.isEmpty()) {
                for (String word : immutableList) {
                    try {
                        responseQueue.put(word);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        @Override
        public synchronized StringIntPair next() {
            try {
                int index = responseQueue.remainingCapacity();
                String word = responseQueue.take();
                return new StringIntPair(word, index);
            } catch (InterruptedException e) {
                return null;
            } finally {
                refillIfEmpty();
            }
        }
    }

    static class ResponderBasic implements Responder {
        private int count = 0;

        private int increment() {
            try {
                return this.count++;
            } finally {
                this.count %= Responder.immutableList.size();
            }
        }

        @Override
        synchronized public StringIntPair next() {
            int index = increment();
            return new StringIntPair(Responder.immutableList.get(index), index);
        }
    }
}

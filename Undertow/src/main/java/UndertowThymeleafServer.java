import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/*
 * mvn clean package exec:java -Dexec.mainClass="UndertowThymeleafServer"
 */

public class UndertowThymeleafServer {

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
            String templateName = "index";

            // Create a Thymeleaf context and add variables
            Context context = new Context();
            context.setVariable("title",
                    "A programmer had a problem. He thought to himself, \"I know, I'll solve it with threads!\"");

            // Process the Thymeleaf template with the given context
            String html = templateEngine.process(templateName, context);

            // Set the response content type and send the rendered HTML as the response
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html;charset=UTF-8");
            exchange.getResponseSender().send(html);
        }
    }

    public static void main(String[] args) {

        // Create a RoutingHandler to handle different paths
        RoutingHandler routingHandler = new RoutingHandler();

        // Add your ThymeleafHandler
        routingHandler.get("/", new ThymeleafHandler());

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(routingHandler)
                .build();
        server.start();
        System.out.println("Server started on port 8080");
    }

}

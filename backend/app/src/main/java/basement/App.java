package basement;
import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws IOException {
        String apiPortStr = System.getenv("PORT");
        System.out.println(apiPortStr);
        int apiPort = Integer.parseInt(apiPortStr);
        HttpServer server = HttpServer.create(new InetSocketAddress(apiPort), 0);

        CassandraConnector client = new CassandraConnector();
        client.connect();
        client.createKeyspace("my_keyspace", "SimpleStrategy", 1);
        client.createTable("posts");

        server.createContext("/post", new CreatePostSummary(client));
        server.createContext("/healthz", new HealthCheckHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port: " + apiPort);

        CqlSession session = client.getSession();
        System.out.println("session " + session);
    }

    static class CreatePostSummary implements HttpHandler {

        CassandraConnector client;

        public CreatePostSummary(CassandraConnector client) {
            this.client = client;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, 0);
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            String response = "Post created: " + requestBody;

            try {
                JSONObject jsonObject = new JSONObject(requestBody);

                String title = jsonObject.getString("title");
                String summary = jsonObject.getString("summary");
                String body  = jsonObject.getString("body");

                if (title == null || summary == null || body == null) {
                    exchange.sendResponseHeaders(422, 0);
                    return;
                }

                client.insertPost(title, summary, body);
                exchange.sendResponseHeaders(201, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } catch (JSONException e) {
                exchange.sendResponseHeaders(500, response.length());
                throw new RuntimeException(e);
            }
        }
    }

    static class GetSummary implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        }
    }

    static class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "Health check OK";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        }
    }
}

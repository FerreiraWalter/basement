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
        Properties properties = loadConfigProperties();
        String apiPortStr = properties.getProperty("API_PORT");
        int apiPort = Integer.parseInt(apiPortStr);
        HttpServer server = HttpServer.create(new InetSocketAddress(apiPort), 0);

        CassandraConnector client = new CassandraConnector();
        client.connect();
        client.createKeyspace("my_keyspace", "SimpleStrategy", 1);
        client.createTable("posts");

        server.createContext("/post", new CreatePostSummary(client));
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
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                String response = "Post created: " + requestBody;

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(requestBody);

                    String title = jsonObject.getString("title");
                    String summary = jsonObject.getString("summary");

                    client.insertPost(title, summary);
                    exchange.sendResponseHeaders(201, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                } catch (JSONException e) {
                    exchange.sendResponseHeaders(500, response.length());
                    throw new RuntimeException(e);
                }

            } else {
                exchange.sendResponseHeaders(405, 0);
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

    public static Properties loadConfigProperties() {
        Properties properties = new Properties();
        String path = "/home/ubots/ubots/mentoria/backend/app/src/main/resources/config.properties";
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}

package basement;
import com.datastax.oss.driver.api.core.CqlSession;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

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

        server.createContext("/post", new CreatePostSummary());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port: " + apiPort);

        CassandraConnector client = new CassandraConnector();
        client.connect();
        CqlSession session = client.getSession();
        System.out.println("session " + session);
        client.close();
    }

    static class CreatePostSummary implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                String response = "Post created: " + requestBody;

                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
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

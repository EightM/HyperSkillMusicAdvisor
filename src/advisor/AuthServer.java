package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AuthServer {
    private HttpServer server;
    private String response = "";

    public AuthServer(int port) {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(MusicAdvisor musicAdvisor) {
        server.createContext("/",
                exchange -> {
                    handleRootRequest(musicAdvisor, exchange);
                }
        );

        server.start();
    }

    private void handleRootRequest(MusicAdvisor musicAdvisor, HttpExchange exchange) throws IOException {
        response = exchange.getRequestURI().getQuery();
        String[] responseArray = response.split("=");
        if (responseArray[0].equals("code")) {
            String success = "Got the code. Return back to your program";
            exchange.sendResponseHeaders(200, success.length());
            exchange.getResponseBody().write(success.getBytes());
            exchange.getResponseBody().close();
            musicAdvisor.endAuthorise(responseArray[1]);
        } else {
            String error = "Not found authorization code. Try again.";
            exchange.sendResponseHeaders(200, error.length());
            exchange.getResponseBody().write(error.getBytes());
            exchange.getResponseBody().close();
        }
    }

    public void stopServer() {
        server.stop(1);
    }

    public String getResponse() {
        return response;
    }
}

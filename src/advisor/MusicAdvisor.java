package advisor;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MusicAdvisor {

    private boolean isAuth = false;
    private static final String CLIENT_ID = "16d31e7e0f764feca09204ca859bb14c";
    private static final String LOGIN_REQUIRE_MESSAGE = "Please, provide access for application.";
    private String spotifyServerAddress = "https://accounts.spotify.com";
    private final AuthServer server = new AuthServer(8080);

    public MusicAdvisor(String spotifyServerAddress) {
        this.spotifyServerAddress = spotifyServerAddress;
    }

    public MusicAdvisor() {
    }

    public void handleInput(String input) {
        String command = "";
        String attribute = "";
        if (input.startsWith("playlists")) {
            command = input.split("\\s")[0];
            attribute = input.split("\\s")[1];
        } else {
            command = input;
        }

        switch (command) {
            case "featured":
                featuredReleases();
                break;
            case "auth":
                beginAuthorise();
                break;
            case "new":
                newAlbums();
                break;
            case "categories":
                categories();
                break;
            case "playlists":
                playlists(attribute);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void exitApp() {
        System.out.println("---GOODBYE!---");
    }

    private void beginAuthorise() {
         System.out.println("use this link to request the access code:");
        String authUrl = String.format(
                "%s/authorize?client_id=%s&redirect_uri=http://localhost:8080&response_type=code",
                spotifyServerAddress,
                CLIENT_ID);
        System.out.println(authUrl);
         // server.startServer(this);
    }

    public void endAuthorise(String code) {
        server.stopServer();
        System.out.println("Code received");
        makeAccessTokenRequest(code);
    }

    private void makeAccessTokenRequest(String code) {
        String bodyRequest = getBodyRequest(code);
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            isAuth = true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private String getBodyRequest(String code) {
        List<String> body = new ArrayList<>();
        body.add("grant_type=authorization_code");
        body.add(String.format("code=%s", code));
        body.add("redirect_uri=http://localhost:8080");
        body.add(String.format("client_id=%s", CLIENT_ID));
        body.add(String.format("client_secret=%s", new StringBuilder(code).reverse().toString()));
        return String.join("&", body);
    }

    private void playlists(String attribute) {

        if (!isAuth) {
            System.out.println(LOGIN_REQUIRE_MESSAGE);
            return;
        }

        System.out.println("---" + attribute.toUpperCase() + " " + "PLAYLISTS---");
        System.out.println("Walk Like A Badass");
        System.out.println("Rage Beats");
        System.out.println("Arab Mood Booster");
        System.out.println("Sunday Stroll");
    }

    private void categories() {

        if (!isAuth) {
            System.out.println(LOGIN_REQUIRE_MESSAGE);
            return;
        }

        System.out.println("---CATEGORIES---");
        System.out.println("Top Lists");
        System.out.println("Pop");
        System.out.println("Mood");
        System.out.println("Latin");
    }

    private void newAlbums() {

        if (!isAuth) {
            System.out.println(LOGIN_REQUIRE_MESSAGE);
            return;
        }

        System.out.println("---NEW RELEASES---");
        System.out.println("Mountains [Sia, Diplo, Labrinth]");
        System.out.println("Runaway [Lil Peep]");
        System.out.println("The Greatest Show [Panic! At The Disco]");
        System.out.println("All Out Life [Slipknot]");
    }

    private void featuredReleases() {

        if (!isAuth) {
            System.out.println(LOGIN_REQUIRE_MESSAGE);
            return;
        }

        System.out.println("---FEATURED---");
        System.out.println("All Out Life [Slipknot]");
        System.out.println("Wake Up and Smell the Coffee");
        System.out.println("Monday Motivation");
        System.out.println("Songs to Sing in the Shower");
    }

}

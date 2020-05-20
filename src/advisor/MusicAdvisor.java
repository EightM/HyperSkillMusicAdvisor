package advisor;

public class MusicAdvisor {

    private boolean isAuth = false;
    private static final String CLIENT_ID = "16d31e7e0f764feca09204ca859bb14c";
    private static final String LOGIN_REQUIRE_MESSAGE = "Please, provide access for application.";

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
                authorise();
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
            case "exit":
                exitApp();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void exitApp() {
        System.out.println("---GOODBYE!---");
    }

    private void authorise() {
        String authUrl = String.format(
                "https://accounts.spotify.com/authorize?client_id=%s&redirect_uri=https://www.example.com&response_type=code",
                CLIENT_ID);
        System.out.println(authUrl);
        System.out.println("---SUCCESS---");
        isAuth = true;
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

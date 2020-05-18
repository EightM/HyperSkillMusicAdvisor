package advisor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        while (!input.equals("exit")) {

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
                case "new":
                    newAlbums();
                    break;
                case "categories":
                    categories();
                    break;
                case "playlists":
                    playlists(attribute);
                    break;
            }

            input = scanner.nextLine();
        }
        exitApp();
    }

    private static void exitApp() {
        System.out.println("---GOODBYE!---");
    }

    private static void playlists(String attribute) {
        System.out.println("---" + attribute.toUpperCase() + " " + "PLAYLISTS---");
        System.out.println("Walk Like A Badass");
        System.out.println("Rage Beats");
        System.out.println("Arab Mood Booster");
        System.out.println("Sunday Stroll");
    }

    private static void categories() {
        System.out.println("---CATEGORIES---");
        System.out.println("Top Lists");
        System.out.println("Pop");
        System.out.println("Mood");
        System.out.println("Latin");
    }

    private static void newAlbums() {
        System.out.println("---NEW RELEASES---");
        System.out.println("Mountains [Sia, Diplo, Labrinth]");
        System.out.println("Runaway [Lil Peep]");
        System.out.println("The Greatest Show [Panic! At The Disco]");
        System.out.println("All Out Life [Slipknot]");
    }

    private static void featuredReleases() {
        System.out.println("---FEATURED---");
        System.out.println("All Out Life [Slipknot]");
        System.out.println("Wake Up and Smell the Coffee");
        System.out.println("Monday Motivation");
        System.out.println("Songs to Sing in the Shower");
    }
}

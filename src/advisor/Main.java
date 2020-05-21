package advisor;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        MusicAdvisor musicAdvisor;
        if (args.length == 2 && args[0].equals("-access")) {
            musicAdvisor = new MusicAdvisor(args[1]);
        } else {
            musicAdvisor = new MusicAdvisor();
        }
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        boolean isExit = false;


        while (!isExit) {
            if (input.equals("exit")) {
                musicAdvisor.exitApp();
                isExit = true;
            }
            musicAdvisor.handleInput(input);
            input = scanner.nextLine();
        }
    }
}

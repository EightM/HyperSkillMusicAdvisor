package advisor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        boolean isExit = false;
        MusicAdvisor musicAdvisor = new MusicAdvisor();

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


import java.util.Random;
import java.util.Scanner;

public class SchereSteinPapier {

    public static void main(String[] args) {
        String[] possibilities = new String[3];
        possibilities[0] = "Schere";
        possibilities[1] = "Stein";
        possibilities[2] = "Papier";
        Random random = new Random();

        System.out.println("Schere(a) Stein(b) Papier(c)?");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        String pc = possibilities[random.nextInt(0, 3)];

        if (input.equals("a")) {
            if (pc.equals("Papier")) {
                System.out.println("You Won!");
            } else if (pc.equals("Schere")) {
                System.out.println("Draw");
            } else {
                System.out.print("You Lose");

            }
        }
        if (input.equals("b")) {
            if (pc.equals("Papier")) {
                System.out.print("You Lose");
            } else if (pc.equals("Schere")) {
                System.out.println("Draw");

            } else {

            }
        }
        if (input.equals("c")) {
            if (pc.equals("Stein")) {
                System.out.println("You Lose");
            } else if (pc.equals("Schere")) {

            } else {
                System.out.print("You Won");

            }
        }

        scanner.close();
        System.out.println("Game is done!");
    }
}

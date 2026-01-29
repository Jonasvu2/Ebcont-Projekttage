
import java.util.Scanner;

public class greeting {
    public static void main(String[] args) {

        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Bitte Namen eingeben");
            String name = input.nextLine();

            System.out.println("Hallo " + name);
        }
    }
}
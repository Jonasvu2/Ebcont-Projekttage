
import java.util.Random;
import java.util.Scanner;

public class DungeonGame {

    public static void main(String[] arg) throws InterruptedException {
        System.out.println("Welcome to Dungeon Game");
        Player player = new Player(100, 50, 1);
        Random random = new Random();

        Thread.sleep(800);
        Scanner scanner = new Scanner(System.in);
        Monster monster = new Monster("Goblin", 50, 15);
        monster.attakPower += random.nextInt(-2, 6);
        monster.printhealth();

        while (player.health > 0 && monster.health > 0) {
            if (player.healthPotions <= 0) {
                System.out.println("Attack (a) or run (b)");
            } else {
                System.out.println("Attack (a) or run (b) or Drink Health Potion (t)");
            }
            String action = scanner.nextLine();

            if (action.equals("a")) {
                System.out.println("You attack the " + monster.name + "!");
                int randomDamage = random.nextInt(-2, 8);
                monster.health -= (player.attack + randomDamage);
                monster.printhealth();
                if (monster.health <= 0) {
                    player.score += 10;
                    System.out.println("Your score:" + player.score);
                    if (Math.random() < 0.5) {
                        player.healthPotions++;
                        System.out.println("You found a Health potion " + player.healthPotions);
                    }
                    if (player.healthPotions > 0) {
                        System.out.println("Do you want to use a health potion? (t) yes (b) no");
                        String usePotion = scanner.nextLine();
                        if (usePotion.equals("t")) {
                            player.drinkHealthPotion();
                        }

                    }
                    if (Math.random() < 0.5) {
                        monster = new Monster("Skeleton", 50, 15);
                    } else {
                        monster = new Monster("Zombie", 100, 20);
                    }
                } else {
                    System.out.print("The Monster attacks you.");
                    player.health -= monster.attakPower;
                    System.out.println(" Your health is " + player.health);
                }
            } else if (action.equals("t")) {
                player.drinkHealthPotion();
            }

            if (action.equals("b")) {
                System.out.println("You run away.And go to a lonly Village");
                break;
            }

        }
        System.out.println(" Game over your Score is " + player.score);
        scanner.close();
    }
}

class Player {

    int health;
    int score;
    int attack;
    int healthPotions;

    public Player(int health, int attack, int Helathpotions) {
        this.health = health;
        this.score = 0;
        this.attack = attack;
        this.healthPotions = Helathpotions;
    }

    public void drinkHealthPotion() {
        Random random = new Random();
        healthPotions--;
        health += random.nextInt(-5, 40);
        System.out.println("Your new Player Health is " + health);
    }
}

class Monster {

    String name;
    int health;
    int attakPower;

    public Monster(String name, int health, int attakPower) {
        this.name = name;
        this.health = health;
        this.attakPower = attakPower;

    }

    public void printhealth() {
        System.out.println("Monster has " + health + " health pionts");

    }

}

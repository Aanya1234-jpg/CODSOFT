import java.util.Random;
import java.util.Scanner;

public class GuessTheNumber {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int minRange = 1;
        int maxRange = 100;
        int secretNumber = random.nextInt(maxRange) + minRange;
        int userGuess;

        System.out.println("I have generated a random number between " + minRange + " and " + maxRange + ". Try to guess it!");

        while (true) {
            System.out.print("Enter your guess: ");
            userGuess = scanner.nextInt();

            if (userGuess < secretNumber) {
                System.out.println("Too low! Try again.");
            } else if (userGuess > secretNumber) {
                System.out.println("Too high! Try again.");
            } else {
                System.out.println("Congratulations! You guessed the correct number: " + secretNumber);
                break;
            }
        }
        
        scanner.close();
    }
}
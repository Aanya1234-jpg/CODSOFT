import java.util.Random;
import java.util.Scanner;

public class AttemptLimit {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int minRange = 1;
        int maxRange = 100;
        int secretNumber = random.nextInt(maxRange) + minRange;
        
        int maxAttempts = 10;
        boolean hasWon = false; 
        System.out.println("Alright, challenger!! My number is hidden somewhere between " + minRange + " and " + maxRange + "Let the guessing begin! ");
        System.out.println("You have " + maxAttempts + " attempts to guess it. Good luck!");
        for (int attempts = 1; attempts <= maxAttempts; attempts++) {
            System.out.print("Attempt #" + attempts + ": Enter your guess: ");
            int userGuess = scanner.nextInt();

            if (userGuess < secretNumber) {
                System.out.println("Too low!");
            } else if (userGuess > secretNumber) {
                System.out.println("Too high!");
            } else {
                System.out.println("Congratulations! You guessed the number!");
                hasWon = true; 
                break; 
            }
        }
        if (!hasWon) {
            System.out.println("Sorry, you've run out of attempts. The number was: " + secretNumber);
        }

        scanner.close();
    }
}
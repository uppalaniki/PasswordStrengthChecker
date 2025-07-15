import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PasswordStrengthChecker {


//Record to store password check result details
    public record CheckResult(String passwordSample, String strength, String advice, LocalDateTime checkedAt) {}

    private static final String CSV_FILE = "password_checks.csv";

    public static void main(String[] args) {
        initializeCSV(); //Setting up the CSV file with headers

        Scanner scanner = new Scanner(System.in);
        System.out.println("Password Strength Checker (type 'exit' to quit)");

        while (true) {
            System.out.print("\nEnter a password to check: ");
            String password = scanner.nextLine();

            //Exit condition
            if (password.equalsIgnoreCase("exit")) break;

            //Evaluating strength and generate advice
            String strength = evaluatePasswordStrength(password);
            String advice = generateAdvice(password);

            //Displaying results
            System.out.println("Strength: " + strength);
            if (!advice.isEmpty()) {
                System.out.println("Advice: " + advice);
            } else {
                System.out.println("Your password looks strong!");
            }

             //Creating result record with masked password
            CheckResult result = new CheckResult(maskPassword(password), strength, advice, LocalDateTime.now());
            logCheckResult(result);
        }

        System.out.println("Program ended.");
        scanner.close();
    }

    //Initializing CSV file with headers
    private static void initializeCSV() {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            writer.write("PasswordSample,Strength,Advice,CheckedAt\n");
        } catch (IOException e) {
            System.err.println("Error initializing CSV file: " + e.getMessage());
        }
    }

    //Appending a check result to the CSV file
    private static void logCheckResult(CheckResult result) {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.write(result.passwordSample() + "," + result.strength() + ",\"" + result.advice() + "\"," + result.checkedAt() + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    //Evaluating password strength based on common criteria
    private static String evaluatePasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (Pattern.compile("[A-Z]").matcher(password).find()) score++;
        if (Pattern.compile("[a-z]").matcher(password).find()) score++;
        if (Pattern.compile("[0-9]").matcher(password).find()) score++;
        if (Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) score++;

        //Returning strength level based on score
        return switch (score) {
            case 5 -> "Very Strong";
            case 4 -> "Strong";
            case 3 -> "Moderate";
            case 2 -> "Weak";
            default -> "Very Weak";
        };
    }

    //Generating advice for improving password based on missing criteria
    private static String generateAdvice(String password) {
        StringBuilder advice = new StringBuilder();

        if (password.length() < 8) {
            advice.append("Increase password length to at least 8 characters. ");
        }
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            advice.append("Add uppercase letters. ");
        }
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            advice.append("Add lowercase letters. ");
        }
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            advice.append("Include numbers. ");
        }
        if (!Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) {
            advice.append("Add special characters (e.g., !, @, #). ");
        }

        return advice.toString().trim();
    }

    //Masking password for logging: show only first and last characters
    private static String maskPassword(String password) {
        if (password.length() <= 2) {
            return "*".repeat(password.length());
        }
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }
}

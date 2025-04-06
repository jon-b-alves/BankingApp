import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\nPick from the following options:");
            System.out.println("1. Create an account");
            System.out.println("2. See your account details");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Please enter your name: ");
                    String name = scanner.nextLine();
                    System.out.print("Please enter initial balance: ");
                    double balance;
                    try {
                        balance = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid balance amount. Please enter a number.");
                        continue;
                    }
                    
                    Account newAccount = new Account(0, name, balance);
                    newAccount.createAccount();
                    break;

                case "2":
                    System.out.print("Please enter your account number: ");
                    try {
                        long accountNumber = Long.parseLong(scanner.nextLine());
                        displayAccountDetails(accountNumber);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid account number format. Please enter a valid number.");
                    }
                    break;

                case "3":
                    System.out.println("Thank you for using our banking system. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    private static void displayAccountDetails(long accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Cannot connect to database");
                return;
            }

            String sql = "SELECT * FROM bank_accounts WHERE account_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, accountNumber);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("\nAccount Details:");
                    System.out.println("Account Number: " + rs.getLong("account_number"));
                    System.out.println("Account Holder: " + rs.getString("account_holder"));
                    System.out.println("Balance: $" + rs.getDouble("balance"));
                } else {
                    System.out.println("Account with number " + accountNumber + " not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account details: " + e.getMessage());
        }
    }
}
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Account {
    private int accountId;
    private long accountNumber; // Generated by generateAccountNumber()
    private String accountHolder;
    private double balance;

    // Updated constructor: accountNumber is optional, set to 0 initially
    public Account(int accountId, String accountHolder, double balance) {
        this.accountId = accountId;
        this.accountNumber = 0; // Default, will be set by generateAccountNumber()
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public long generateAccountNumber() throws SQLException {
        Random random = new Random();
        long min = 100000000000L;
        long max = 999999999999L;
        int maxAttempts = 10;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Database connection is null");
            }

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                long accountNumber = min + (long) (random.nextDouble() * (max - min + 1));
                String sql = "SELECT COUNT(*) FROM bank_accounts WHERE account_number = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, accountNumber);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) == 0) {
                        this.accountNumber = accountNumber; // Assign to field
                        return accountNumber;
                    }
                }
            }
            throw new SQLException("Failed to generate a unique account number after " + maxAttempts + " attempts");
        }
    }

    public void createAccount() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO bank_accounts (account_number, account_holder, balance) VALUES (?, ?, ?)";
            
            try (
                PreparedStatement pstmt = conn.prepareStatement(sql)
            ) {
                this.generateAccountNumber(); // Generate and set accountNumber

                pstmt.setLong(1, this.accountNumber);
                pstmt.setString(2, this.accountHolder);
                pstmt.setDouble(3, this.balance);

                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Account for " + this.accountHolder + " with number " + this.accountNumber + " created successfully!");
                } else {
                    System.out.println("Failed to create account.");
                }

            } catch (SQLException e) {
                System.out.println("Error creating account: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Failed to create account due to database connection issue.");
        }
    }
}
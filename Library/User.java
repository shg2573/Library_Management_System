package Library;
import java.sql.*;
import java.util.Scanner;

public class User {
    private Connection conn;
    public User(Connection conn) {
        this.conn = conn;
    }
    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("----------------------");
            System.out.println("User Panel:");
            System.out.println("1. Borrow Book");
            System.out.println("2. Deposit Book");
            System.out.println("3. View My Transactions");
            System.out.println("4. Exit");
            System.out.println("----------------------");
            Books book=new Books(conn);
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.println("----------------------");
            switch (choice) {
                case 1:
                    book.borrowBooks(scanner);
                    break;
                case 2:
                    book.returnBooks(scanner);
                    break;
                case 3:
                    myTransaction(scanner);
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void myTransaction(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        int userId = scanner.nextInt();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Transactions WHERE MemberID = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("----------------------");
                System.out.println("Transaction ID: " + rs.getInt("TransactionID"));
                System.out.println("Book ID: " + rs.getInt("BookID"));
                System.out.println("Borrowed Date: " + rs.getTimestamp("IssueDate"));
                System.out.println("Return Date: " + rs.getDate("ReturnDate"));
                if (rs.getDate("ReturnDate") == null) {
                    System.out.println("Status: Borrowed");
                } else {
                    System.out.println("Status: Returned");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

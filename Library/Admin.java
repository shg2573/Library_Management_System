package Library;
import java.sql.*;
import java.util.Scanner;


public class Admin {
    private static Connection conn;
    public Admin(Connection conn) {
        this.conn = conn;
    }
    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--------------------");
            System.out.println("Admin Panel:");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. View Transaction");
            System.out.println("4. Register User");
            System.out.println("5. Exit");
            System.out.println("--------------------");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    addBook(scanner);
                    break;
                case 2:
                    viewBooks(scanner);
                    break;
                case 3:
                    viewTransactions();
                    break;
                case 4:
                    addMember(scanner);
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    /*CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Author VARCHAR(255),
    ISBN VARCHAR(13) UNIQUE,
    Publisher VARCHAR(255),
    Edition VARCHAR(50),
    Genre VARCHAR(100),
    NumberOfCopies INT NOT NULL,
    ShelfLocation VARCHAR(100),
    Status ENUM('Available', 'Issued') DEFAULT 'Available'
);*/
    private void addBook(Scanner scanner) {
        Books book=new Books(conn);
        book.addBook(scanner);
    }
    private void viewBooks(Scanner scanner) {
        Books book=new Books(conn);
        book.viewBooks(scanner);
    }
    private void viewTransactions() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Transactions");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Print the retrieved data
                System.out.println("----------------------");
                System.out.println("TransactionID: " + rs.getInt("TransactionID"));
                System.out.println("MemberID: " + rs.getInt("MemberID"));
                System.out.println("BookID: " + rs.getInt("BookID"));
                System.out.println("IssueDate: " + rs.getDate("IssueDate"));
                System.out.println("DueDate: " + rs.getDate("DueDate"));
                System.out.println("ReturnDate: " + rs.getDate("ReturnDate"));
                //System.out.println("----------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void addMember(Scanner scanner) {
                System.out.print("Enter Member's Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Member's Address: ");
                String address = scanner.nextLine();
                System.out.print("Enter Member's Phone Number ");
                String phno = scanner.nextLine();
                System.out.print("Enter Member's Email ID: ");
                String email= scanner.nextLine();
                System.out.println("-----------------------");
                try {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Members (Name, Address, PhoneNumber, Email) " +
                            "VALUES (?, ?, ?, ?)");
                    stmt.setString(1, name);
                    stmt.setString(2, address);
                    stmt.setString(3, phno);
                    stmt.setString(4, email);
                    stmt.executeUpdate();
                    stmt.close();
                    System.out.println("A new member has been inserted successfully!");
                } catch (SQLException e) {
                    System.out.println("----------------------");
                    System.out.println("Registration Failed");
                }
        }

}


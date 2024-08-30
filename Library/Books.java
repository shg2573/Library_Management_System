package Library;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Books {
    private static Connection conn;
    public Books(Connection conn) {
        this.conn = conn;
    }
    // ADD BOOKS TO THE TABLE
    public void addBook(Scanner scanner) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Publisher: ");
        String publisher= scanner.nextLine();
        System.out.print("Enter Edition: ");
        String edition = scanner.nextLine();
        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter Shelf number: ");
        String shno = scanner.nextLine();
        System.out.println("-----------------------");
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Books (Title, Author, ISBN, Publisher,Edition,Genre,ShelfLocation) VALUES (?, ?,?,?,?,?,?)");
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, isbn);
            stmt.setString(4, publisher);
            stmt.setString(5, edition);
            stmt.setString(6, genre);
            stmt.setString(7, shno);
            stmt.executeUpdate();
            stmt.close();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.out.println("!!ERROR!! Please Retry");
        }
    }
    public void viewBooks(Scanner scanner) {
        try {
            System.out.println("----------------------");
            System.out.println("1. All Books");
            System.out.println("2. Available Books");
            System.out.println("----------------------");
            int ch=scanner.nextInt();
            if(ch==1){
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    System.out.println("----------------------");
                    System.out.println("Book ID: " + rs.getInt("BookId"));
                    System.out.println("Title: " + rs.getString("Title"));
                    System.out.println("Author: " + rs.getString("Author"));
                    System.out.println("Publisher: " + rs.getString("Publisher"));
                    System.out.println("Genre: " + rs.getString("Genre"));
                    System.out.println("Shelf Number: " + rs.getString("ShelfLocation"));
                    System.out.println("Status: " + rs.getString("Status"));
                    System.out.println("----------------------");
                }stmt.close();
            }
            else if(ch==2){
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books WHERE Status=?");
                stmt.setString(1,"Available");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    System.out.println("----------------------");
                    System.out.println("Book ID: " + rs.getInt("BookId"));
                    System.out.println("Title: " + rs.getString("Title"));
                    System.out.println("Author: " + rs.getString("Author"));
                    System.out.println("Publisher: " + rs.getString("Publisher"));
                    System.out.println("Genre: " + rs.getString("Genre"));
                    System.out.println("Shelf Number: " + rs.getString("ShelfLocation"));
                    System.out.println("Status: " + rs.getString("Status"));
                    System.out.println("----------------------");
                }stmt.close();
            }
            else System.out.println("Wrong choice");
        } catch (SQLException e) {
            System.out.println("!!ERROR!! Please Retry");
        }
    }
    public void borrowBooks(Scanner scanner){
        System.out.println("----------------------");
        System.out.print("Enter Book ID: ");
        int bookId=scanner.nextInt();

        try {
            PreparedStatement checkStmt= conn.prepareStatement("SELECT Status FROM Books WHERE BookID = ?");
                checkStmt.setInt(1, bookId);

                ResultSet rs = checkStmt.executeQuery();
                if(rs.next() && rs.getString("Status").equals("Available")){
                    String query = "INSERT INTO Transactions (MemberID, BookID, DueDate) VALUES (?, ?, ?)";
                    System.out.print("Enter User ID: ");
                    int memberId=scanner.nextInt();
                    System.out.println("Enter Return Date (yyyy-mm-dd)");
                    String dueDate=scanner.next();
                    System.out.println("----------------------");
                    try {
                       conn.setAutoCommit(false);
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setInt(1, memberId);
                        stmt.setInt(2, bookId);
                        stmt.setString(3, dueDate);  // Assumes dueDate is passed as a String in "YYYY-MM-DD" format

                        stmt.executeUpdate();
                        PreparedStatement stmt1 = conn.prepareStatement("update Books set Status=? where BookID=?");
                        stmt1.setString(1, "Issued");
                        stmt1.setInt(2, bookId);
                        stmt1.executeUpdate();
                        PreparedStatement stmt2 = conn.prepareStatement("update Members set TotalBooksIssued=? where MemberID=?");
                        PreparedStatement bookIssued= conn.prepareStatement("select TotalBooksIssued from Members where MemberID=?");
                        bookIssued.setInt(1,memberId);
                        ResultSet book= bookIssued.executeQuery();
                        while(book.next()){
                            stmt2.setInt(1, book.getInt("TotalBooksIssued") + 1);
                            stmt2.setInt(2, memberId);
                            stmt2.executeUpdate();
                        }
                        conn.commit();
                        System.out.println("Book successfully Borrowed.");
                        conn.setAutoCommit(true);

                    }catch (SQLException e){
                        conn.rollback();
                        System.out.println("Unsuccessful Please Retry ");
                    }
                }else {
                    System.out.println("----------------------");
                    System.out.println("Book Is Not Available");
                    System.out.println("----------------------");
                }
            } catch (SQLException e) {
            System.out.println("!!ERROR!!");

        }
    }
    public void returnBooks(Scanner scanner){
        try {
            conn.setAutoCommit(false);
            System.out.println("----------------------");
            System.out.print("Enter Book ID: ");
            int bookId=scanner.nextInt();
            System.out.print("Enter User ID: ");
            int memberId=scanner.nextInt();
            System.out.print("Enter Transaction ID: ");
            int transId=scanner.nextInt();
            System.out.println("----------------------");
            String query = "UPDATE Transactions SET ReturnDate=? where MemberId=? AND BookID=? AND TransactionId=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            stmt.setInt(2, memberId);
            stmt.setInt(3, bookId);
            stmt.setInt(4, transId);
            stmt.executeUpdate();
            PreparedStatement stmt1 = conn.prepareStatement("update Books set Status=? where BookID=?");
            stmt1.setString(1, "Available");
            stmt1.setInt(2, bookId);
            stmt1.executeUpdate();
            PreparedStatement stmt2 = conn.prepareStatement("update Members set TotalBooksIssued=? where MemberID=?");
            PreparedStatement bookIssued= conn.prepareStatement("select TotalBooksIssued from Members where MemberID=?");
            bookIssued.setInt(1,memberId);
            ResultSet book= bookIssued.executeQuery();
            while(book.next()){
                stmt2.setInt(1, book.getInt("TotalBooksIssued") - 1);
                stmt2.setInt(2, memberId);
                stmt2.executeUpdate();
            }
            PreparedStatement trans=conn.prepareStatement("select * from Transactions where MemberID=? and BookID=? and TransactionID=?");
            trans.setInt(1, memberId);
            trans.setInt(2, bookId);
            trans.setInt(3, transId);
            double fine = 0.0;
            ResultSet tranH=trans.executeQuery();
            while(tranH.next()){
                if (tranH.getDate("ReturnDate").toLocalDate().isAfter(tranH.getDate("DueDate").toLocalDate())) {
                long daysLate = ChronoUnit.DAYS.between(tranH.getDate("ReturnDate").toLocalDate(), tranH.getDate("DueDate").toLocalDate());
                fine = daysLate * 10.0;  // Assume a fine of 10 currency units per day
                System.out.println("Fine is Rupees "+fine);
                }}
            conn.commit();
            System.out.println("Book successfully Returned.");
            System.out.println("----------------------");
            conn.setAutoCommit(true);

        }catch (SQLException e){
            try{conn.rollback();
            System.out.println("Unsuccessful Please Retry ");}
            catch (SQLException f){
                System.out.println("ERROR");
            }
        }
    }
}

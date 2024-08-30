import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import Library.*;
public class Main {
    public static void main(String[] args) {
        try {
            // Initialize database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "root");
            Scanner ob=new Scanner(System.in);
            // Initialize panels
            Admin adminPanel = new Admin(conn);
            User userPanel = new User(conn);
            // Display panels
            while(true){
                System.out.println("------------------------");
                System.out.println("1. Admin Panel");
                System.out.println("2. User Panel");
                System.out.println("3. Exit");
                System.out.println("------------------------");
                int n=ob.nextInt();
                if(n==1)adminPanel.display();
                else if(n==2)userPanel.display();
                else if(n==3)break;
                else{ System.out.println("Invalid Choice try Again");
                    System.out.println("------------------------");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

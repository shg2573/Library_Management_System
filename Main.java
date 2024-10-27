import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Library.*;
public class Main {
    private static Connection conn;

    public static void main(String[] args) {
        try {
            // Initialize database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "root");

            // Create the main frame

            JFrame frame = new JFrame("Library Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
            JLabel headingLabel = new JLabel("LIBRARY MANAGEMENT SYSTEM");
            headingLabel.setFont(new Font("Arial", Font.BOLD, 32));
            // Main panel with centered layout for buttons
            JPanel panel = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            panel.add(headingLabel, gbc);
            // Admin Panel Button
            JButton adminButton = new JButton("Admin Panel");
            adminButton.setPreferredSize(new Dimension(200, 50));
            adminButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Admin(conn).setVisible(true);
                }
            });
            panel.add(adminButton, gbc);

            // User Panel Button
            JButton userButton = new JButton("User Panel");
            userButton.setPreferredSize(new Dimension(200, 50));
            userButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new User(conn).setVisible(true);
                }
            });
            panel.add(userButton, gbc);

            // Exit Button
            JButton exitButton = new JButton("Exit");
            exitButton.setPreferredSize(new Dimension(200, 50));
            exitButton.addActionListener(e -> System.exit(0));
            panel.add(exitButton, gbc);

            // Add the panel to the frame
            frame.add(panel);
            frame.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed.");
        }
    }
}

// Admin Panel class


// User Panel class

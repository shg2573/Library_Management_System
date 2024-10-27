package Library;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class User extends JFrame {
    public User(Connection conn) {

        setTitle("User Panel");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY); // Optional background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Add a top-center heading
        JLabel headingLabel = new JLabel("User Panel");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headingLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Buttons in two columns
        JButton borrowBookButton = new JButton("Borrow Book");
        borrowBookButton.setPreferredSize(new Dimension(150, 40));
        borrowBookButton.addActionListener(e -> {
            new BorrowBookPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(borrowBookButton, gbc);

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.setPreferredSize(new Dimension(150, 40));
        returnBookButton.addActionListener(e -> {
            new ReturnBookPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(returnBookButton, gbc);

        JButton viewMyTransactionButton = new JButton("Transactions");
        viewMyTransactionButton.setPreferredSize(new Dimension(150, 40));
        viewMyTransactionButton.addActionListener(e -> {
            new TransactionHistoryPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(viewMyTransactionButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> dispose()); // Closes AdminPanel and returns to main screen
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(backButton, gbc);

        // Add the panel to the frame
        add(panel);
        setVisible(true);
    }
}
class BorrowBookPanel extends JFrame {
    public BorrowBookPanel(Connection conn) {
        setTitle("Borrow Book");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel heading = new JLabel("Borrow Book", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField bookIdField = new JTextField(20);
        JTextField memberIdField = new JTextField(20);
        JTextField dueDateField = new JTextField(20);

        // Layout the fields and labels
        addLabelAndField(panel, gbc, "Book ID", bookIdField);
        addLabelAndField(panel, gbc, "User ID", memberIdField);
        addLabelAndField(panel, gbc, "Due Date (yyyy-mm-dd)", dueDateField);

        // Buttons at the bottom right
        JPanel buttonPanel = new JPanel();
        JButton borrowButton = new JButton("Borrow Book");
        JButton backButton = new JButton("Back");

        borrowButton.addActionListener(e -> {
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText());
            String dueDate = dueDateField.getText();

            try {
                conn.setAutoCommit(false);
                PreparedStatement checkStmt = conn.prepareStatement("SELECT Status FROM Books WHERE BookID = ?");
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && "Available".equals(rs.getString("Status"))) {
                    // Insert transaction
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO Transactions (MemberID, BookID, DueDate) VALUES (?, ?, ?)");
                    stmt.setInt(1, memberId);
                    stmt.setInt(2, bookId);
                    stmt.setString(3, dueDate);
                    stmt.executeUpdate();

                    // Update book status
                    PreparedStatement updateBookStmt = conn.prepareStatement(
                            "UPDATE Books SET Status = ? WHERE BookID = ?");
                    updateBookStmt.setString(1, "Issued");
                    updateBookStmt.setInt(2, bookId);
                    updateBookStmt.executeUpdate();

                    // Update member’s total books issued
                    PreparedStatement updateMemberStmt = conn.prepareStatement(
                            "UPDATE Members SET TotalBooksIssued = TotalBooksIssued + 1 WHERE MemberID = ?");
                    updateMemberStmt.setInt(1, memberId);
                    updateMemberStmt.executeUpdate();

                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Book successfully borrowed.");
                } else {
                    JOptionPane.showMessageDialog(this, "Book is not available.");
                }
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction failed. Please retry.");
                } catch (SQLException rollbackEx) {
                    JOptionPane.showMessageDialog(this, "Error: " + rollbackEx.getMessage());
                }
            }
        });

        backButton.addActionListener(e -> dispose());
        buttonPanel.add(borrowButton);
        buttonPanel.add(backButton);

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);
    }
}
class TransactionHistoryPanel extends JFrame {
    public TransactionHistoryPanel(Connection conn) {
        setTitle("My Transactions");
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel userIdLabel = new JLabel("Enter your User ID: ");
        JTextField userIdField = new JTextField(10);
        JButton fetchButton = new JButton("Fetch Transactions");

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(userIdLabel);
        inputPanel.add(userIdField);
        inputPanel.add(fetchButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea transactionArea = new JTextArea();
        transactionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        fetchButton.addActionListener(e -> {
            int userId;
            try {
                userId = Integer.parseInt(userIdField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid User ID.");
                return;
            }

            transactionArea.setText("");
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Transactions WHERE MemberID = ?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("Transaction ID: ").append(rs.getInt("TransactionID")).append("\n");
                    sb.append("Book ID: ").append(rs.getInt("BookID")).append("\n");
                    sb.append("Issue Date: ").append(rs.getTimestamp("IssueDate")).append("\n");
                    sb.append("Return Date: ").append(rs.getDate("ReturnDate") == null ? "Not Returned" : rs.getDate("ReturnDate")).append("\n");
                    sb.append("Status: ").append(rs.getDate("ReturnDate") == null ? "Borrowed" : "Returned").append("\n");
                    sb.append("----------------------\n");
                }

                if (sb.length() == 0) {
                    sb.append("No transactions found for User ID ").append(userId).append(".");
                }

                transactionArea.setText(sb.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching transactions.");
            }
        });

        add(panel);
        setVisible(true);
    }
}
class ReturnBookPanel extends JFrame {
    public ReturnBookPanel(Connection conn) {
        setTitle("Return Book");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel heading = new JLabel("Return Book", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField bookIdField = new JTextField(20);
        JTextField memberIdField = new JTextField(20);
        JTextField transactionIdField = new JTextField(20);

        // Layout the fields and labels
        addLabelAndField(panel, gbc, "Book ID", bookIdField);
        addLabelAndField(panel, gbc, "User ID", memberIdField);
        addLabelAndField(panel, gbc, "Transaction ID", transactionIdField);

        // Buttons at the bottom right
        JPanel buttonPanel = new JPanel();
        JButton returnButton = new JButton("Return Book");
        JButton backButton = new JButton("Back");

        returnButton.addActionListener(e -> {
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText());
            int transactionId = Integer.parseInt(transactionIdField.getText());

            try {
                conn.setAutoCommit(false);

                // Update return date
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Transactions SET ReturnDate = ? WHERE MemberId = ? AND BookID = ? AND TransactionId = ?");
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, memberId);
                stmt.setInt(3, bookId);
                stmt.setInt(4, transactionId);
                stmt.executeUpdate();

                // Update book status
                PreparedStatement updateBookStmt = conn.prepareStatement(
                        "UPDATE Books SET Status = 'Available' WHERE BookID = ?");
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                // Update member’s total books issued
                PreparedStatement updateMemberStmt = conn.prepareStatement(
                        "UPDATE Members SET TotalBooksIssued = TotalBooksIssued - 1 WHERE MemberID = ?");
                updateMemberStmt.setInt(1, memberId);
                updateMemberStmt.executeUpdate();

                // Calculate fine if overdue
                PreparedStatement transStmt = conn.prepareStatement(
                        "SELECT DueDate, ReturnDate FROM Transactions WHERE MemberID = ? AND BookID = ? AND TransactionID = ?");
                transStmt.setInt(1, memberId);
                transStmt.setInt(2, bookId);
                transStmt.setInt(3, transactionId);
                ResultSet rs = transStmt.executeQuery();

                if (rs.next() && rs.getDate("ReturnDate").toLocalDate().isAfter(rs.getDate("DueDate").toLocalDate())) {
                    long daysLate = java.time.temporal.ChronoUnit.DAYS.between(
                            rs.getDate("DueDate").toLocalDate(), rs.getDate("ReturnDate").toLocalDate());
                    double fine = daysLate * 10.0;  // Assume a fine of 10 currency units per day
                    JOptionPane.showMessageDialog(this, "Fine is Rupees " + fine);
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Book successfully returned.");
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction failed. Please retry.");
                } catch (SQLException rollbackEx) {
                    JOptionPane.showMessageDialog(this, "Error: " + rollbackEx.getMessage());
                }
            }
        });

        backButton.addActionListener(e -> dispose());
        buttonPanel.add(returnButton);
        buttonPanel.add(backButton);

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);
    }
}


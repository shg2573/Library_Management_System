package Library;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends JFrame {
    public Admin(Connection conn) {
        setTitle("Admin Panel");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY); // Optional background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Add a top-center heading
        JLabel headingLabel = new JLabel("Admin Panel");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headingLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Buttons in two columns
        JButton addBookButton = new JButton("Add Book");
        addBookButton.setPreferredSize(new Dimension(150, 40));
        addBookButton.addActionListener(e -> {
            new AddBookPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(addBookButton, gbc);

        JButton deleteBookButton = new JButton("Delete Book");
        deleteBookButton.setPreferredSize(new Dimension(150, 40));
        deleteBookButton.addActionListener(e -> {
            new DeleteBookPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(deleteBookButton, gbc);

        JButton updateBookButton = new JButton("Update Book");
        updateBookButton.setPreferredSize(new Dimension(150, 40));
        updateBookButton.addActionListener(e -> {
            new UpdateBookPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(updateBookButton, gbc);

        JButton viewBooksButton = new JButton("View Books");
        viewBooksButton.setPreferredSize(new Dimension(150, 40));
        viewBooksButton.addActionListener(e -> {
            new ViewBooksPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(viewBooksButton, gbc);

        // New buttons
        JButton viewTransactionsButton = new JButton("View All Transactions");
        viewTransactionsButton.setPreferredSize(new Dimension(150, 40));
        viewTransactionsButton.addActionListener(e -> {
            new ViewTransactionsPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(viewTransactionsButton, gbc);

        JButton addUserButton = new JButton("Add User");
        addUserButton.setPreferredSize(new Dimension(150, 40));
        addUserButton.addActionListener(e -> {
            new AddUserPanel(conn).setVisible(true);
            dispose();
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(addUserButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> dispose()); // Closes AdminPanel and returns to main screen
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(backButton, gbc);

        // Add the panel to the frame
        add(panel);
        setVisible(true);
    }
}

// Placeholder panels for the new features
class AddBookPanel extends JFrame {
    public AddBookPanel(Connection conn) {
        setTitle("Add Book");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;

        JLabel heading = new JLabel("Add Book", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField editionField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextField shelfField = new JTextField(20);

        // Add components in vertical stack with labels right-aligned
        addLabelAndField(panel, gbc, "Title", titleField);
        addLabelAndField(panel, gbc, "Author", authorField);
        addLabelAndField(panel, gbc, "ISBN", isbnField);
        addLabelAndField(panel, gbc, "Publisher", publisherField);
        addLabelAndField(panel, gbc, "Edition", editionField);
        addLabelAndField(panel, gbc, "Genre", genreField);
        addLabelAndField(panel, gbc, "Shelf Location", shelfField);

        // Buttons at the bottom right
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton backButton = new JButton("Back");
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(e -> {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Books (Title, Author, ISBN, Publisher, Edition, Genre, ShelfLocation) VALUES (?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, titleField.getText());
                stmt.setString(2, authorField.getText());
                stmt.setString(3, isbnField.getText());
                stmt.setString(4, publisherField.getText());
                stmt.setString(5, editionField.getText());
                stmt.setString(6, genreField.getText());
                stmt.setString(7, shelfField.getText());
                stmt.executeUpdate();
                stmt.close();
                JOptionPane.showMessageDialog(this, "Book added successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            new Admin(conn).setVisible(true);
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        // Layout configuration
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
class ViewBooksPanel extends JFrame {
    public ViewBooksPanel(Connection conn) {
        setTitle("View Books");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("View Books", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea booksText = new JTextArea();
        booksText.setEditable(false);
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            new Admin(conn).setVisible(true);
            dispose();
        });

        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(booksText), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        // Load all books from the database
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
            ResultSet rs = stmt.executeQuery();
            StringBuilder booksData = new StringBuilder();
            while (rs.next()) {
                booksData.append("Book ID: ").append(rs.getInt("BookId"))
                        .append("\nTitle: ").append(rs.getString("Title"))
                        .append("\nAuthor: ").append(rs.getString("Author"))
                        .append("\nPublisher: ").append(rs.getString("Publisher"))
                        .append("\nGenre: ").append(rs.getString("Genre"))
                        .append("\nShelf Number: ").append(rs.getString("ShelfLocation"))
                        .append("\nStatus: ").append(rs.getString("Status"))
                        .append("\n----------------------\n");
            }
            booksText.setText(booksData.toString());
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        add(panel);
        setVisible(true);
    }
}

//ViewTransactionPAnel
class ViewTransactionsPanel extends JFrame {
    public ViewTransactionsPanel(Connection conn) {
        setTitle("View All Transactions");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("View All Transactions", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea transactionsText = new JTextArea();
        transactionsText.setEditable(false);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            new Admin(conn).setVisible(true);
            dispose();
        });

        // Load all transactions from the database
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Transactions");
            ResultSet rs = stmt.executeQuery();
            StringBuilder transactionsData = new StringBuilder();
            while (rs.next()) {
                transactionsData.append("TransactionID: ").append(rs.getInt("TransactionID"))
                        .append("\nMemberID: ").append(rs.getInt("MemberID"))
                        .append("\nBookID: ").append(rs.getInt("BookID"))
                        .append("\nIssueDate: ").append(rs.getDate("IssueDate"))
                        .append("\nDueDate: ").append(rs.getDate("DueDate"))
                        .append("\nReturnDate: ").append(rs.getDate("ReturnDate"))
                        .append("\n----------------------\n");
            }
            transactionsText.setText(transactionsData.toString());
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(transactionsText), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

//AddUserPanel
class AddUserPanel extends JFrame {
    public AddUserPanel(Connection conn) {
        setTitle("Add Member");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;

        JLabel heading = new JLabel("Add New Member", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        // Add components in vertical stack with labels right-aligned
        addLabelAndField(panel, gbc, "Name", nameField);
        addLabelAndField(panel, gbc, "Address", addressField);
        addLabelAndField(panel, gbc, "Phone Number", phoneField);
        addLabelAndField(panel, gbc, "Email", emailField);

        // Buttons at the bottom right
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Member");
        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Members (Name, Address, PhoneNumber, Email) VALUES (?, ?, ?, ?)");
                stmt.setString(1, nameField.getText());
                stmt.setString(2, addressField.getText());
                stmt.setString(3, phoneField.getText());
                stmt.setString(4, emailField.getText());
                stmt.executeUpdate();
                stmt.close();
                JOptionPane.showMessageDialog(this, "Member added successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            new Admin(conn).setVisible(true);
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        // Layout configuration
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
class DeleteBookPanel extends JFrame {
    public DeleteBookPanel(Connection conn) {
        setTitle("Delete Book");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new JLabel("Delete Book Panel To be added soon")); // Customize as needed
    }
}

class UpdateBookPanel extends JFrame {
    public UpdateBookPanel(Connection conn) {
        setTitle("Update Book");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new JLabel("Update Book Panel")); // Customize as needed
    }
}



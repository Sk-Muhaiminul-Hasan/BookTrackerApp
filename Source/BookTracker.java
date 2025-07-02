import java.sql.*;
import java.util.Scanner;

public class BookTracker {
    private Scanner scanner = new Scanner(System.in);

    public User login() {
        System.out.print("Enter username: ");
        String name = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE name = ? AND password = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), name);
            } else {
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    public void register() {
        System.out.print("Choose username: ");
        String name = scanner.nextLine();
        System.out.print("Choose password: ");
        String password = scanner.nextLine();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (name, password) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    public void addBook(User user) {
        System.out.print("Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Progress (% completed): ");
        int progress = scanner.nextInt();
        scanner.nextLine(); // consume newline

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO books (user_id, title, author, progress) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setInt(4, progress);
            pstmt.executeUpdate();
            System.out.println("Book added!");
        } catch (SQLException e) {
            System.out.println("Add book error: " + e.getMessage());
        }
    }

    public void viewBooks(User user) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM books WHERE user_id = ?")) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nYour Books:");
            while (rs.next()) {
                System.out.println(rs.getString("title") + " by " + rs.getString("author") +
                        " - " + rs.getInt("progress") + "% read");
            }
        } catch (SQLException e) {
            System.out.println("View books error: " + e.getMessage());
        }
    }
}

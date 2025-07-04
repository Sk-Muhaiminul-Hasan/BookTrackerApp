import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookTracker {
    public User login(String username, String password) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE name = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (name, password) VALUES (?, ?)")) {
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password.trim());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBook(User user, String title, String author, int progress) {
        if (user == null || title == null || title.trim().isEmpty() ||
                author == null || author.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO books (user_id, title, author, progress) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, title.trim());
            pstmt.setString(3, author.trim());
            pstmt.setInt(4, progress);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> getUserBooks(User user) {
        List<Book> books = new ArrayList<>();
        if (user == null) return books;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM books WHERE user_id = ? ORDER BY title")) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("progress")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
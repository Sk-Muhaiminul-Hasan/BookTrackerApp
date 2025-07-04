import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:books.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load JDBC driver: " + e.getMessage());
        }

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL);";

            String booksTable = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "title TEXT," +
                    "author TEXT," +
                    "progress INTEGER," +
                    "FOREIGN KEY(user_id) REFERENCES users(id));";

            stmt.execute(usersTable);
            stmt.execute(booksTable);

        } catch (SQLException e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }
}
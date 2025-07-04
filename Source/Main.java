public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.initializeDatabase();

        // Launch the GUI application
        javax.swing.SwingUtilities.invokeLater(() -> {
            new BookTrackerGUI();
        });
    }
}
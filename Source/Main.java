import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        Scanner scanner = new Scanner(System.in);
        BookTracker tracker = new BookTracker();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Book Tracker ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    tracker.register();
                    break;
                case 2:
                    User user = tracker.login();
                    if (user != null) {
                        boolean loggedIn = true;
                        while (loggedIn) {
                            System.out.println("\nWelcome, " + user.getName() + "!");
                            System.out.println("1. Add Book");
                            System.out.println("2. View My Books");
                            System.out.println("3. Logout");
                            System.out.print("Choose: ");
                            int opt = scanner.nextInt();
                            scanner.nextLine();

                            switch (opt) {
                                case 1: tracker.addBook(user); break;
                                case 2: tracker.viewBooks(user); break;
                                case 3: loggedIn = false; break;
                                default: System.out.println("Invalid option.");
                            }
                        }
                    }
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
        System.out.println("Goodbye!");
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BookTrackerGUI {
    private JFrame mainFrame;
    private BookTracker bookTracker;
    private User currentUser;
    private JPanel cards;

    // Theme variables
    private boolean darkMode = false;
    private Color lightBackground = new Color(248, 249, 250);
    private Color darkBackground = new Color(60, 63, 65);
    private Color buttonBlue = new Color(59, 89, 152);
    private Color darkButtonBlue = new Color(100, 130, 200);

    // Card identifiers
    private static final String LOGIN = "LOGIN";
    private static final String MAIN = "MAIN";
    private static final String ADD_BOOK = "ADD_BOOK";
    private static final String VIEW_BOOKS = "VIEW_BOOKS";

    public BookTrackerGUI() {
        bookTracker = new BookTracker();
        initialize();
    }

    private void initialize() {
        setupLookAndFeel();

        mainFrame = new JFrame("MyBookList");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 650);
        mainFrame.setMinimumSize(new Dimension(800, 600));
        mainFrame.setLocationRelativeTo(null);

        try {
            mainFrame.setIconImage(new ImageIcon(getClass().getResource("/book-icon.png")).getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        // Create card panel
        cards = new JPanel(new CardLayout());
        cards.add(createLoginPanel(), LOGIN);
        cards.add(createMainPanel(), MAIN);
        cards.add(createAddBookPanel(), ADD_BOOK);
        cards.add(createViewBooksPanel(), VIEW_BOOKS);

        mainFrame.add(cards, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCard(String cardName) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, cardName);
        applyTheme(); // Re-apply theme when switching panels
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(lightBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("MyBookList", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(buttonBlue);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField userText = new JTextField(15);
        styleTextField(userText);
        panel.add(userText, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passText = new JPasswordField(15);
        styleTextField(passText);
        panel.add(passText, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        stylePrimaryButton(loginBtn);
        loginBtn.addActionListener(e -> {
            currentUser = bookTracker.login(userText.getText(), new String(passText.getPassword()));
            if (currentUser != null) {
                showCard(MAIN);
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Invalid username or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton registerBtn = new JButton("Register");
        styleSecondaryButton(registerBtn);
        registerBtn.addActionListener(e -> {
            if (bookTracker.register(userText.getText(), new String(passText.getPassword()))) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Registration successful! Please login.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Registration failed. Username may be taken.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        panel.add(buttonPanel, gbc);

        // Add enter key listener
        passText.addActionListener(e -> loginBtn.doClick());

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightBackground);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(buttonBlue);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Theme toggle button
        JButton themeBtn = new JButton(darkMode ? "☀️ Light" : "🌙 Dark");
        themeBtn.addActionListener(e -> toggleTheme());
        styleSecondaryButton(themeBtn);

        JButton logoutBtn = new JButton("Logout");
        styleSecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            showCard(LOGIN);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(themeBtn);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(logoutBtn);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Update welcome label when panel is shown
        headerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
            }
        });

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(lightBackground);

        JButton addBookBtn = new JButton("Add New Book");
        stylePrimaryButton(addBookBtn);
        addBookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBookBtn.addActionListener(e -> showCard(ADD_BOOK));

        JButton viewBooksBtn = new JButton("View My Books");
        stylePrimaryButton(viewBooksBtn);
        viewBooksBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewBooksBtn.addActionListener(e -> showCard(VIEW_BOOKS));

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(addBookBtn);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(viewBooksBtn);
        contentPanel.add(Box.createVerticalGlue());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(lightBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Add New Book");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(buttonBlue);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Book Title
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel bookTitleLabel = new JLabel("Title:");
        bookTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(bookTitleLabel, gbc);

        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        styleTextField(titleField);
        panel.add(titleField, gbc);

        // Author
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(authorLabel, gbc);

        gbc.gridx = 1;
        JTextField authorField = new JTextField(20);
        styleTextField(authorField);
        panel.add(authorField, gbc);

        // Progress
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel progressLabel = new JLabel("Progress (%):");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(progressLabel, gbc);

        gbc.gridx = 1;
        JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        progressSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(progressSpinner, "#");
        progressSpinner.setEditor(editor);
        panel.add(progressSpinner, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton submitBtn = new JButton("Submit");
        stylePrimaryButton(submitBtn);
        submitBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            int progress = (Integer) progressSpinner.getValue();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (bookTracker.addBook(currentUser, title, author, progress)) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Book added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                titleField.setText("");
                authorField.setText("");
                progressSpinner.setValue(0);
                showCard(MAIN);
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Failed to add book",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleSecondaryButton(cancelBtn);
        cancelBtn.addActionListener(e -> showCard(MAIN));

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createViewBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightBackground);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(buttonBlue);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("My Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Theme toggle button
        JButton themeBtn = new JButton(darkMode ? "☀️ Light" : "🌙 Dark");
        themeBtn.addActionListener(e -> toggleTheme());
        styleSecondaryButton(themeBtn);

        JButton backBtn = new JButton("Back");
        styleSecondaryButton(backBtn);
        backBtn.addActionListener(e -> showCard(MAIN));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(themeBtn);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(backBtn);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Book List
        DefaultListModel<Book> listModel = new DefaultListModel<>();
        JList<Book> bookList = new JList<>(listModel);
        bookList.setCellRenderer(new BookListRenderer());
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setBackground(darkMode ? darkBackground : Color.WHITE);
        bookList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(bookList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Update list when panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                listModel.clear();
                List<Book> books = bookTracker.getUserBooks(currentUser);
                for (Book book : books) {
                    listModel.addElement(book);
                }
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(darkMode ? darkButtonBlue : buttonBlue);
        button.setForeground(Color.WHITE); // White text for primary buttons
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode ? new Color(80, 110, 180) : new Color(40, 70, 120)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(darkMode ? new Color(80, 80, 80) : new Color(240, 240, 240));
        button.setForeground(buttonBlue); // Blue text for secondary buttons
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode ? new Color(100, 100, 100) : new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode ? new Color(100, 100, 100) : new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(darkMode ? new Color(80, 80, 80) : Color.WHITE);
        field.setForeground(darkMode ? Color.WHITE : Color.BLACK);
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    private void applyTheme() {
        Color bgColor = darkMode ? darkBackground : lightBackground;
        Color textColor = darkMode ? Color.WHITE : Color.BLACK;

        // Apply to all components
        for (Component comp : mainFrame.getContentPane().getComponents()) {
            applyThemeToComponent(comp, bgColor, textColor);
        }

        // Update buttons
        updateButtonStyles();
    }

    private void applyThemeToComponent(Component comp, Color bgColor, Color textColor) {
        if (comp instanceof Container) {
            for (Component child : ((Container)comp).getComponents()) {
                applyThemeToComponent(child, bgColor, textColor);
            }
        }

        if (comp instanceof JPanel) {
            comp.setBackground(bgColor);
        }
        if (comp instanceof JLabel) {
            ((JLabel)comp).setForeground(textColor);
        }
        if (comp instanceof JList) {
            comp.setBackground(darkMode ? darkBackground : Color.WHITE);
        }
    }

    private void updateButtonStyles() {
        // Update all buttons in the application
        updateButtonStylesInContainer(mainFrame.getContentPane());
    }

    private void updateButtonStylesInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;

                if(button.getBackground().equals(buttonBlue)){
                    stylePrimaryButton(button);
                }else{
                    styleSecondaryButton(button);
                }

            }
            if (comp instanceof Container) {
                updateButtonStylesInContainer((Container) comp);
            }
        }
    }

    class BookListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Set colors based on theme
            if (darkMode) {
                setBackground(isSelected ? new Color(80, 80, 80) : darkBackground);
                setForeground(Color.WHITE);
            } else {
                setBackground(isSelected ? new Color(220, 230, 240) : Color.WHITE);
                setForeground(Color.BLACK);
            }

            if (value instanceof Book) {
                Book book = (Book) value;
                setText(String.format("<html><b>%s</b> by %s<br>Progress: %d%%</html>",
                        book.getTitle(), book.getAuthor(), book.getProgress()));

                if (book.isRead()) {
                    setIcon(new ImageIcon(getClass().getResource("/checkmark.png")));
                    setForeground(new Color(100, 200, 100)); // Light green for completed
                }
            }

            return this;
        }
    }
}
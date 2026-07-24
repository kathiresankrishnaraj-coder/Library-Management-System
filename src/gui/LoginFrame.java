package gui;

import components.*;
import model.User;
import service.UserManagement;
import theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private UserManagement userService = new UserManagement();
    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JCheckBox chkShowPassword;
    private RoundedButton btnLogin;
    private RoundedButton btnExit;

    public LoginFrame() {
        // Initialize Look and Feel configuration first
        ThemeManager.init();

        setTitle("Library Management System ERP - Login Console");
        setUndecorated(true);
        
        // Requirement: Open in full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Root container panel with dynamic gradient background
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Modern professional gradient backdrop
                GradientPaint gp = new GradientPaint(0, 0, new Color(24, 32, 54), getWidth(), getHeight(), new Color(42, 58, 92));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative ambient background circles
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(-100, -100, 400, 400);
                g2.fillOval(getWidth() - 250, getHeight() - 250, 500, 500);
                g2.dispose();
            }
        };
        // Requirement: Center all components in responsive layout
        bgPanel.setLayout(new GridBagLayout());

        // Center Login Card Panel
        RoundedPanel cardPanel = new RoundedPanel(24);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(420, 560));
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(35, 40, 35, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Logo
        JLabel lblLogo = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 144, 255));
                g2.fillOval(x, y, 64, 64);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                g2.drawString("L", x + 23, y + 43);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 64; }
            @Override public int getIconHeight() { return 64; }
        });
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        cardPanel.add(lblLogo, gbc);

        // Requirement: Show application title
        JLabel lblTitle = new JLabel("LIBRARY ERP SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        gbc.gridy = 1;
        cardPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("University & Public Library Management", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = 2;
        cardPanel.add(lblSubtitle, gbc);

        // Input Fields Container
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 1, 0, 14));
        fieldsPanel.setOpaque(false);

        // Requirement: Display username and password fields with icons
        txtUsername = new RoundedTextField();
        txtUsername.setPlaceholder("Enter Username");
        txtUsername.setPrefixIcon(createGlyphIcon("👤"));

        txtPassword = new RoundedPasswordField();
        txtPassword.setPlaceholder("Enter Password");
        txtPassword.setPrefixIcon(createGlyphIcon("🔒"));

        cbRole = new JComboBox<>(new String[]{"Admin", "Librarian"});
        cbRole.setPreferredSize(new Dimension(100, 40));
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        fieldsPanel.add(txtUsername);
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(cbRole);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 10, 0);
        cardPanel.add(fieldsPanel, gbc);

        // Options Row (Show Password + Forgot Password)
        JPanel checkRow = new JPanel(new BorderLayout());
        checkRow.setOpaque(false);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setForeground(new Color(100, 116, 139));
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        JLabel lblForgot = new JLabel("Forgot password?", SwingConstants.RIGHT);
        lblForgot.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblForgot.setForeground(new Color(24, 144, 255));
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                ToastNotification.show(LoginFrame.this, "Contact your administrator to reset credentials.", ToastNotification.Type.INFO);
            }
        });

        checkRow.add(chkShowPassword, BorderLayout.WEST);
        checkRow.add(lblForgot, BorderLayout.EAST);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(checkRow, gbc);

        // Requirement: Stylish Login button & Exit button
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);

        btnExit = new RoundedButton("Exit (Esc)", new Color(226, 232, 240), new Color(203, 213, 225));
        btnExit.setForeground(new Color(51, 65, 85));
        btnExit.setPreferredSize(new Dimension(100, 44));
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.addActionListener(e -> exitApplication());

        btnLogin = new RoundedButton("Login (Enter)", new Color(24, 144, 255), new Color(11, 101, 192));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(100, 44));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.addActionListener(e -> attemptLogin());

        btnPanel.add(btnExit);
        btnPanel.add(btnLogin);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        cardPanel.add(btnPanel, gbc);

        bgPanel.add(cardPanel);
        setContentPane(bgPanel);

        // Requirement: Keyboard shortcuts (Enter -> Login, Esc -> Exit)
        setupKeyboardShortcuts();
    }

    private Icon createGlyphIcon(String glyph) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                g2.setColor(new Color(100, 116, 139));
                g2.drawString(glyph, x + 2, y + 14);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
        };
    }

    private void setupKeyboardShortcuts() {
        // Enter -> Login
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER_LOGIN");
        getRootPane().getActionMap().put("ENTER_LOGIN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Esc -> Exit
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE_EXIT");
        getRootPane().getActionMap().put("ESCAPE_EXIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });
    }

    private void exitApplication() {
        System.exit(0);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            // Requirement: Show an error dialog for invalid credentials
            JOptionPane.showMessageDialog(this,
                    "Please fill in both Username and Password fields.",
                    "Login Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            ToastNotification.show(this, "Please enter both credentials", ToastNotification.Type.ERROR);
            return;
        }

        // Show loading spinner dialog
        LoadingDialog loading = new LoadingDialog(this, "Verifying credentials...");
        
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Simulate network latency for loading overlay
                Thread.sleep(600);
                return userService.loginUser(username, password);
            }

            @Override
            protected void done() {
                loading.dispose();
                try {
                    User loggedIn = get();
                    if (loggedIn != null && loggedIn.getRole().equalsIgnoreCase(role)) {
                        ToastNotification.show(LoginFrame.this, "Authentication success!", ToastNotification.Type.SUCCESS);
                        
                        // Close login, launch Main Workspace frame
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(loggedIn).setVisible(true);
                            dispose();
                        });
                    } else {
                        // Requirement: Show an error dialog for invalid credentials
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid Username, Password, or Role!\nPlease verify your credentials and try again.",
                                "Authentication Error",
                                JOptionPane.ERROR_MESSAGE);
                        ToastNotification.show(LoginFrame.this, "Invalid credentials or mismatching role", ToastNotification.Type.ERROR);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Database connection failure.\nPlease check database server status.",
                            "System Error",
                            JOptionPane.ERROR_MESSAGE);
                    ToastNotification.show(LoginFrame.this, "Database connection error", ToastNotification.Type.ERROR);
                }
            }
        }.execute();

        loading.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

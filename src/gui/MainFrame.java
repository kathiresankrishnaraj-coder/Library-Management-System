package gui;

import components.*;
import model.User;
import service.NotificationManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private User currentUser;
    private NotificationManagement notificationService = new NotificationManagement();

    private CardLayout cardLayout;
    private JPanel centerContentCards;
    
    private HeaderPanel headerPanel;
    private FooterPanel footerPanel;

    // Sidebar buttons
    private SidebarButton btnDashboard;
    private SidebarButton btnBooks;
    private SidebarButton btnStudents;
    private SidebarButton btnIssue;
    private SidebarButton btnReturn;
    private SidebarButton btnReservations;
    private SidebarButton btnPurchase;
    private SidebarButton btnReports;
    private SidebarButton btnNotifications;
    private SidebarButton btnUsers;
    private SidebarButton btnSettings;
    private SidebarButton btnAbout;
    private SidebarButton btnLogout;

    // Panels
    private DashboardPanel dashboardPanel;
    private BookManagementPanel bookPanel;
    private StudentManagementPanel studentPanel;
    private IssueBookPanel issuePanel;
    private ReturnBookPanel returnPanel;
    private ReservationPanel reservationPanel;
    private PurchasePanel purchasePanel;
    private ReportsPanel reportsPanel;
    private NotificationPanel notificationPanel;
    private UserManagementPanel userPanel;
    private SettingsPanel settingsPanel;
    private AboutPanel aboutPanel;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Library Management System ERP - Workspace");
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Core Layout Panel
        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(new Color(245, 246, 248));

        // 1. Sidebar (Left Panel)
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(30, 30, 40)); // Sleek dark slate
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Sidebar Brand title
        JLabel lblBrand = new JLabel("LMS ERP Workspace");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setBorder(new EmptyBorder(25, 25, 25, 25));
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblBrand);

        // Sidebar items
        btnDashboard = new SidebarButton("Dashboard", createTextIcon("📊"));
        btnBooks = new SidebarButton("Books Repository", createTextIcon("📖"));
        btnStudents = new SidebarButton("Students Directory", createTextIcon("🎓"));
        btnIssue = new SidebarButton("Issue Book", createTextIcon("📤"));
        btnReturn = new SidebarButton("Return Book", createTextIcon("📥"));
        btnReservations = new SidebarButton("Reservations", createTextIcon("⏳"));
        btnPurchase = new SidebarButton("Acquisitions", createTextIcon("🛒"));
        btnReports = new SidebarButton("Reports Center", createTextIcon("📈"));
        btnNotifications = new SidebarButton("System Alerts", createTextIcon("🔔"));
        btnUsers = new SidebarButton("System Operators", createTextIcon("👤"));
        btnSettings = new SidebarButton("Preferences", createTextIcon("⚙️"));
        btnAbout = new SidebarButton("About System", createTextIcon("ℹ️"));
        btnLogout = new SidebarButton("Log Out", createTextIcon("🚪"));

        // Alignments
        Component[] menuItems = {btnDashboard, btnBooks, btnStudents, btnIssue, btnReturn, btnReservations, btnPurchase, btnReports, btnNotifications, btnUsers, btnSettings, btnAbout, btnLogout};
        for (Component c : menuItems) {
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // Add menu items to sidebar
        sidebar.add(btnDashboard);
        sidebar.add(btnBooks);
        sidebar.add(btnStudents);
        sidebar.add(btnIssue);
        sidebar.add(btnReturn);
        sidebar.add(btnReservations);
        sidebar.add(btnPurchase);
        sidebar.add(btnReports);
        sidebar.add(btnNotifications);

        // Role-based authorization
        if (currentUser.getRole().equalsIgnoreCase("Admin")) {
            sidebar.add(btnUsers);
        }

        sidebar.add(btnSettings);
        sidebar.add(btnAbout);
        sidebar.add(Box.createVerticalGlue()); // Push logout to bottom
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        mainLayout.add(sidebar, BorderLayout.WEST);

        // 2. Center Panel (Header + Main Cards Content + Footer)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        headerPanel = new HeaderPanel("Dashboard Analytics", currentUser.getUsername(), currentUser.getRole());
        headerPanel.addNotificationClickListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchCard("ALERTS");
            }
        });
        centerPanel.add(headerPanel, BorderLayout.NORTH);

        // CardLayout Content Container
        cardLayout = new CardLayout();
        centerContentCards = new JPanel(cardLayout);
        centerContentCards.setOpaque(false);

        // Instantiate Sub-panels
        dashboardPanel = new DashboardPanel();
        bookPanel = new BookManagementPanel();
        studentPanel = new StudentManagementPanel();
        issuePanel = new IssueBookPanel();
        returnPanel = new ReturnBookPanel();
        reservationPanel = new ReservationPanel();
        purchasePanel = new PurchasePanel();
        reportsPanel = new ReportsPanel();
        notificationPanel = new NotificationPanel();
        settingsPanel = new SettingsPanel();
        aboutPanel = new AboutPanel();

        centerContentCards.add(dashboardPanel, "DASHBOARD");
        centerContentCards.add(bookPanel, "BOOKS");
        centerContentCards.add(studentPanel, "STUDENTS");
        centerContentCards.add(issuePanel, "ISSUE");
        centerContentCards.add(returnPanel, "RETURN");
        centerContentCards.add(reservationPanel, "RESERVATIONS");
        centerContentCards.add(purchasePanel, "PURCHASES");
        centerContentCards.add(reportsPanel, "REPORTS");
        centerContentCards.add(notificationPanel, "ALERTS");
        centerContentCards.add(settingsPanel, "SETTINGS");
        centerContentCards.add(aboutPanel, "ABOUT");

        if (currentUser.getRole().equalsIgnoreCase("Admin")) {
            userPanel = new UserManagementPanel();
            centerContentCards.add(userPanel, "USERS");
        }

        centerPanel.add(centerContentCards, BorderLayout.CENTER);

        // Footer status bar
        footerPanel = new FooterPanel();
        centerPanel.add(footerPanel, BorderLayout.SOUTH);

        mainLayout.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainLayout);

        // Button Event Listeners
        btnDashboard.addActionListener(e -> switchCard("DASHBOARD"));
        btnBooks.addActionListener(e -> switchCard("BOOKS"));
        btnStudents.addActionListener(e -> switchCard("STUDENTS"));
        btnIssue.addActionListener(e -> switchCard("ISSUE"));
        btnReturn.addActionListener(e -> switchCard("RETURN"));
        btnReservations.addActionListener(e -> switchCard("RESERVATIONS"));
        btnPurchase.addActionListener(e -> switchCard("PURCHASES"));
        btnReports.addActionListener(e -> switchCard("REPORTS"));
        btnNotifications.addActionListener(e -> switchCard("ALERTS"));
        btnSettings.addActionListener(e -> switchCard("SETTINGS"));
        btnAbout.addActionListener(e -> switchCard("ABOUT"));

        if (currentUser.getRole().equalsIgnoreCase("Admin")) {
            btnUsers.addActionListener(e -> switchCard("USERS"));
        }

        btnLogout.addActionListener(e -> {
            boolean confirm = ConfirmationDialog.show(this, "Logout Session", "Are you sure you want to log out?");
            if (confirm) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // Set default card active
        switchCard("DASHBOARD");
        updateNotificationCount();
    }

    public void switchCard(String name) {
        cardLayout.show(centerContentCards, name);
        
        // Deactivate all sidebar highlight buttons
        btnDashboard.setActive(false);
        btnBooks.setActive(false);
        btnStudents.setActive(false);
        btnIssue.setActive(false);
        btnReturn.setActive(false);
        btnReservations.setActive(false);
        btnPurchase.setActive(false);
        btnReports.setActive(false);
        btnNotifications.setActive(false);
        btnSettings.setActive(false);
        btnAbout.setActive(false);
        if (btnUsers != null) btnUsers.setActive(false);

        // Activate and update title for the selected tab
        switch (name) {
            case "DASHBOARD":
                btnDashboard.setActive(true);
                headerPanel.setTitle("Dashboard Analytics");
                dashboardPanel.refreshData();
                break;
            case "BOOKS":
                btnBooks.setActive(true);
                headerPanel.setTitle("Book Inventory Records");
                bookPanel.refreshTable();
                break;
            case "STUDENTS":
                btnStudents.setActive(true);
                headerPanel.setTitle("Students Directory");
                studentPanel.refreshTable();
                break;
            case "ISSUE":
                btnIssue.setActive(true);
                headerPanel.setTitle("Issue Book Checkout");
                break;
            case "RETURN":
                btnReturn.setActive(true);
                headerPanel.setTitle("Return Book Check-in");
                break;
            case "RESERVATIONS":
                btnReservations.setActive(true);
                headerPanel.setTitle("Reservations Waitlist");
                reservationPanel.refreshTable();
                break;
            case "PURCHASES":
                btnPurchase.setActive(true);
                headerPanel.setTitle("Acquisitions Stock Log");
                purchasePanel.refreshTable();
                break;
            case "REPORTS":
                btnReports.setActive(true);
                headerPanel.setTitle("Reports & Exports Center");
                break;
            case "ALERTS":
                btnNotifications.setActive(true);
                headerPanel.setTitle("System Messages & Alerts");
                notificationPanel.refreshTable();
                break;
            case "USERS":
                if (btnUsers != null) {
                    btnUsers.setActive(true);
                    headerPanel.setTitle("Operator Accounts Privileges");
                    userPanel.refreshTable();
                }
                break;
            case "SETTINGS":
                btnSettings.setActive(true);
                headerPanel.setTitle("Preferences & Maintenance Tools");
                break;
            case "ABOUT":
                btnAbout.setActive(true);
                headerPanel.setTitle("About LMS ERP Suite");
                break;
        }
        updateNotificationCount();
    }

    public void updateNotificationCount() {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return notificationService.getUnreadCount();
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    headerPanel.setNotificationCount(count);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private Icon createTextIcon(String glyph) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                g2.setColor(Color.WHITE);
                g2.drawString(glyph, x, y + 14);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 18; }
            @Override public int getIconHeight() { return 18; }
        };
    }
}

package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderPanel extends JPanel {

    private JLabel lblTitle;
    private JLabel lblUser;
    private NotificationBadge badge;
    private JButton btnNotification;

    public HeaderPanel(String defaultTitle, String username, String role) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 30, 15, 30));

        // Left Panel: Title
        lblTitle = new JLabel(defaultTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        add(lblTitle, BorderLayout.WEST);

        // Right Panel: Notifications & Profile Info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);

        // Notification Bell Icon drawn in Java2D / Unicode
        Icon bellIcon = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 100, 100));
                
                // Draw a beautiful Bell using shapes
                g2.fillRoundRect(x + 6, y + 16, 12, 4, 2, 2); // base
                int[] px = {x + 4, x + 20, x + 16, x + 8};
                int[] py = {y + 16, y + 16, y + 6, y + 6};
                g2.fillPolygon(px, py, 4); // body
                g2.fillOval(x + 10, y + 2, 4, 4); // top loop
                g2.fillOval(x + 10, y + 18, 4, 4); // clapper
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 24;
            }

            @Override
            public int getIconHeight() {
                return 24;
            }
        };

        badge = new NotificationBadge(bellIcon);
        badge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Profile panel
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setOpaque(false);

        lblUser = new JLabel(username + " (" + role + ")");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(80, 80, 80));

        JLabel lblAvatar = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 144, 255));
                g2.fillOval(x, y, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String initials = username.substring(0, Math.min(username.length(), 2)).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, x + (32 - fm.stringWidth(initials)) / 2, y + (32 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 32;
            }

            @Override
            public int getIconHeight() {
                return 32;
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        profilePanel.add(lblUser, gbc);
        
        gbc.gridx = 1;
        profilePanel.add(lblAvatar, gbc);

        rightPanel.add(badge);
        rightPanel.add(profilePanel);

        add(rightPanel, BorderLayout.EAST);
    }

    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    public void setNotificationCount(int count) {
        badge.setCount(count);
    }

    public void addNotificationClickListener(java.awt.event.MouseAdapter adapter) {
        badge.addMouseListener(adapter);
    }

    public void setProfile(String username, String role) {
        lblUser.setText(username + " (" + role + ")");
        repaint();
    }
}

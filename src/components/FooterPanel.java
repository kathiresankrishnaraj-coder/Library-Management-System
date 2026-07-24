package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FooterPanel extends JPanel {

    private JLabel lblDateTime;
    private JLabel lblDbStatus;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FooterPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 30, 10, 30));

        // Left Side: Copyright
        JLabel lblCopy = new JLabel("© 2026 Library Management System ERP. All Rights Reserved.");
        lblCopy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCopy.setForeground(new Color(140, 140, 140));
        add(lblCopy, BorderLayout.WEST);

        // Center / Right Side: Database Status & Time
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statusContainer.setOpaque(false);

        lblDbStatus = new JLabel("Database: Connected", new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(76, 175, 80)); // Green dot
                g2.fillOval(x, y + 2, 8, 8);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 12;
            }
        }, SwingConstants.LEFT);
        lblDbStatus.setIconTextGap(8);
        lblDbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDbStatus.setForeground(new Color(120, 120, 120));

        lblDateTime = new JLabel(dateFormat.format(new Date()));
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDateTime.setForeground(new Color(120, 120, 120));

        statusContainer.add(lblDbStatus);
        statusContainer.add(lblDateTime);

        add(statusContainer, BorderLayout.EAST);

        // Start timer to update time every second
        Timer timer = new Timer(1000, e -> {
            lblDateTime.setText(dateFormat.format(new Date()));
        });
        timer.start();
    }

    public void setDatabaseStatus(boolean connected) {
        if (connected) {
            lblDbStatus.setText("Database: Connected");
            lblDbStatus.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(76, 175, 80)); // Green dot
                    g2.fillOval(x, y + 2, 8, 8);
                    g2.dispose();
                }
                @Override
                public int getIconWidth() { return 12; }
                @Override
                public int getIconHeight() { return 12; }
            });
        } else {
            lblDbStatus.setText("Database: Disconnected");
            lblDbStatus.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(244, 67, 54)); // Red dot
                    g2.fillOval(x, y + 2, 8, 8);
                    g2.dispose();
                }
                @Override
                public int getIconWidth() { return 12; }
                @Override
                public int getIconHeight() { return 12; }
            });
        }
    }
}

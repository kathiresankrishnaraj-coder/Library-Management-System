package gui;

import components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;

public class AboutPanel extends JPanel {

    public AboutPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JLabel lblHeader = new JLabel("About Academy Library System");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(60, 60, 60));
        add(lblHeader, BorderLayout.NORTH);

        // Core Card Panel
        RoundedPanel mainPanel = new RoundedPanel(16);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(35, 35, 35, 35));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        // Logo Representation in Java2D Vector
        JLabel lblLogo = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 144, 255));
                g2.fillRoundRect(x, y, 70, 70, 20, 20);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3.0f));
                // Draw book profile lines
                g2.drawRoundRect(x + 15, y + 15, 40, 40, 8, 8);
                g2.drawLine(x + 35, y + 15, x + 35, y + 55);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 70; }
            @Override public int getIconHeight() { return 70; }
        });
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(lblLogo, gbc);

        JLabel lblTitle = new JLabel("LMS ERP Suite", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        gbc.gridy = 1;
        mainPanel.add(lblTitle, gbc);

        JLabel lblVersion = new JLabel("Version 2.4.0-RELEASE (Build 2026)", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVersion.setForeground(Color.GRAY);
        gbc.gridy = 2;
        mainPanel.add(lblVersion, gbc);

        // Tech specs grid
        JPanel specs = new JPanel(new GridLayout(4, 2, 10, 8));
        specs.setOpaque(false);
        specs.setBorder(new EmptyBorder(15, 40, 15, 40));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font valFont = new Font("Segoe UI", Font.BOLD, 13);

        specs.add(createLabel("Programming Language:", labelFont));
        specs.add(createLabel("Java SE 21 (LTS)", valFont));

        specs.add(createLabel("Database Management:", labelFont));
        specs.add(createLabel("MySQL Server 8.0 & JDBC", valFont));

        specs.add(createLabel("Graphical Subsystem:", labelFont));
        specs.add(createLabel("Java Swing with FlatLaf Theme", valFont));

        specs.add(createLabel("Architecture Pattern:", labelFont));
        specs.add(createLabel("Model-View-Controller (MVC)", valFont));

        gbc.gridy = 3;
        mainPanel.add(specs, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        gbc.gridy = 4;
        mainPanel.add(sep, gbc);

        // External Link Triggers
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        linksPanel.setOpaque(false);

        RoundedButton btnGitHub = new RoundedButton("Open GitHub Repo", new Color(40, 40, 40), new Color(20, 20, 20));
        btnGitHub.setPreferredSize(new Dimension(160, 35));
        btnGitHub.addActionListener(e -> openUrl("https://github.com/"));

        RoundedButton btnLinkedIn = new RoundedButton("LinkedIn Developer Profile", new Color(10, 102, 194), new Color(9, 85, 160));
        btnLinkedIn.setPreferredSize(new Dimension(200, 35));
        btnLinkedIn.addActionListener(e -> openUrl("https://linkedin.com/"));

        linksPanel.add(btnGitHub);
        linksPanel.add(btnLinkedIn);
        gbc.gridy = 5;
        mainPanel.add(linksPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(new Color(80, 80, 80));
        return l;
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Failed to launch browser", ToastNotification.Type.ERROR);
        }
    }
}

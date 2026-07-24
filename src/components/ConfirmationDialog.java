package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfirmationDialog extends JDialog {

    private boolean confirmed = false;

    public ConfirmationDialog(Frame owner, String title, String message) {
        super(owner, title, true);
        setUndecorated(true);
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setLayout(new BorderLayout(15, 15));

        // Header icon / text
        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(50, 50, 50));
        mainPanel.add(lblHeader, BorderLayout.NORTH);

        // Body message
        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMessage.setForeground(new Color(80, 80, 80));
        lblMessage.setBorder(new EmptyBorder(10, 0, 10, 0));
        mainPanel.add(lblMessage, BorderLayout.CENTER);

        // Actions panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(220, 220, 220), new Color(200, 200, 200));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        RoundedButton btnConfirm = new RoundedButton("Confirm", new Color(24, 144, 255), new Color(11, 101, 192));
        btnConfirm.setPreferredSize(new Dimension(95, 35));
        btnConfirm.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        actionPanel.add(btnCancel);
        actionPanel.add(btnConfirm);
        
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setSize(Math.max(getWidth(), 320), getHeight());
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static boolean show(Frame owner, String title, String message) {
        ConfirmationDialog dlg = new ConfirmationDialog(owner, title, message);
        dlg.setVisible(true);
        return dlg.isConfirmed();
    }
}


package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoadingDialog extends JDialog {

    private JLabel lblMessage;

    public LoadingDialog(Frame owner, String message) {
        super(owner, true);
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        // Custom background panel using BoxLayout for vertical stacking
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMessage.setForeground(new Color(60, 60, 60));
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 6));
        progressBar.setMaximumSize(new Dimension(200, 6));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setForeground(new Color(24, 144, 255));

        panel.add(lblMessage);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(progressBar);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    public void setMessage(String message) {
        lblMessage.setText(message);
        pack();
    }
}

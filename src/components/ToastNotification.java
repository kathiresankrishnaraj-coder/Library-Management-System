package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ToastNotification extends JWindow {

    public enum Type { SUCCESS, ERROR, INFO }

    private int duration = 3000; // 3 seconds
    private Type type;
    private String message;

    public ToastNotification(Frame owner, String message, Type type) {
        super(owner);
        this.message = message;
        this.type = type;

        setAlwaysOnTop(true);
        
        // Background and text color based on type
        Color bg, fg;
        Icon icon;
        if (type == Type.SUCCESS) {
            bg = new Color(230, 247, 236);
            fg = new Color(39, 174, 96);
            icon = new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(39, 174, 96));
                    g2.fillOval(x, y, 18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawLine(x + 5, y + 9, x + 8, y + 12);
                    g2.drawLine(x + 8, y + 12, x + 13, y + 5);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        } else if (type == Type.ERROR) {
            bg = new Color(253, 237, 237);
            fg = new Color(235, 87, 87);
            icon = new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(235, 87, 87));
                    g2.fillOval(x, y, 18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawLine(x + 5, y + 5, x + 13, y + 13);
                    g2.drawLine(x + 13, y + 5, x + 5, y + 13);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        } else {
            bg = new Color(230, 247, 255);
            fg = new Color(24, 144, 255);
            icon = new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(24, 144, 255));
                    g2.fillOval(x, y, 18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2.drawString("i", x + 8, y + 13);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        }

        JPanel panel = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(240, 240, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblIcon = new JLabel(icon);
        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMessage.setForeground(fg);

        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(lblMessage, BorderLayout.CENTER);
        
        setContentPane(panel);
        pack();

        // Position at bottom-right of owner
        if (owner != null) {
            int x = owner.getX() + owner.getWidth() - getWidth() - 25;
            int y = owner.getY() + owner.getHeight() - getHeight() - 65;
            setLocation(x, y);
        } else {
            // Position on screen
            Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(scrSize.width - getWidth() - 25, scrSize.height - getHeight() - 80);
        }
    }

    public static void show(Frame owner, String message, Type type) {
        ToastNotification toast = new ToastNotification(owner, message, type);
        toast.setVisible(true);

        Timer timer = new Timer(toast.duration, e -> {
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}

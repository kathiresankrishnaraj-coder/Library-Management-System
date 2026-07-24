package components;

import javax.swing.*;
import java.awt.*;

public class NotificationBadge extends JLabel {

    private int count = 0;
    private Color badgeColor = new Color(239, 83, 80); // Modern Coral Red

    public NotificationBadge(Icon icon) {
        super(icon);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (count > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String text = count > 99 ? "99+" : String.valueOf(count);
            Font font = new Font("Segoe UI", Font.BOLD, 10);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            
            int padding = 4;
            int badgeW = fm.stringWidth(text) + padding * 2;
            int badgeH = fm.getHeight();

            // Circular/Oval dimensions
            int size = Math.max(badgeW, badgeH);
            int x = getWidth() - size - 2;
            int y = 2;

            // Draw red badge bubble
            g2.setColor(badgeColor);
            g2.fillOval(x, y, size, size);

            // Draw border around the badge to pop it
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawOval(x, y, size, size);

            // Draw count text inside bubble
            g2.setColor(Color.WHITE);
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}

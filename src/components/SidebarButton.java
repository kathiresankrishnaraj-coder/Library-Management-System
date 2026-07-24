package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {

    private boolean active = false;
    private boolean hovered = false;
    
    private Color activeBg = new Color(24, 144, 255, 30); // Semi-transparent blue
    private Color activeFg = new Color(24, 144, 255);
    private Color normalBg = new Color(0, 0, 0, 0); // Transparent
    private Color normalFg = new Color(160, 160, 160);
    private Color hoverBg = new Color(255, 255, 255, 10);
    private Color hoverFg = Color.WHITE;

    public SidebarButton(String text, Icon icon) {
        super(text, icon);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(15);
        setBorder(new EmptyBorder(12, 20, 12, 20));
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setForeground(normalFg);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        setForeground(active ? activeFg : normalFg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background paint
        if (active) {
            g2.setColor(activeBg);
            g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 8, 8, 8);
            
            // Left blue active indicator line
            g2.setColor(activeFg);
            g2.fillRoundRect(8, 8, 4, getHeight() - 16, 2, 2);
        } else if (hovered) {
            g2.setColor(hoverBg);
            g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 8, 8, 8);
            setForeground(hoverFg);
        } else {
            setForeground(normalFg);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

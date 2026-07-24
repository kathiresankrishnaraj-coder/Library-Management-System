package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private int radius = 12;
    private Color normalColor = new Color(30, 136, 229); // Accent Blue
    private Color hoverColor = new Color(21, 101, 192);
    private Color activeColor = new Color(13, 71, 161);
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public RoundedButton(String text, Color baseColor, Color hoverColor) {
        this(text);
        this.normalColor = baseColor;
        this.hoverColor = hoverColor;
        this.activeColor = hoverColor.darker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isEnabled()) {
            if (isPressed) {
                g2.setColor(activeColor);
            } else if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(normalColor);
            }
        } else {
            g2.setColor(Color.LIGHT_GRAY);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();

        super.paintComponent(g);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setBaseColors(Color normal, Color hover) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.activeColor = hover.darker();
        repaint();
    }
}

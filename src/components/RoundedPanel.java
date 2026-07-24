package components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {

    private int cornerRadius = 15;
    private Color shadowColor = new Color(0, 0, 0, 15);
    private boolean showShadow = true;

    public RoundedPanel() {
        super();
        setOpaque(false);
    }

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.cornerRadius = radius;
        setBackground(bgColor);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow first if enabled
        if (showShadow) {
            graphics.setColor(shadowColor);
            graphics.fillRoundRect(2, 2, width - 4, height - 4, arcs.width, arcs.height);
        }

        // Draw background
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - (showShadow ? 3 : 1), height - (showShadow ? 3 : 1), arcs.width, arcs.height);
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
    }

    public boolean isShowShadow() {
        return showShadow;
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
        repaint();
    }
}

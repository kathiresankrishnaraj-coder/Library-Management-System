package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RoundedTextField extends JTextField {

    private String placeholder = "";
    private Icon prefixIcon = null;
    private int radius = 12;
    private Color borderColor = new Color(200, 200, 200);
    private Color focusColor = new Color(30, 136, 229);
    private boolean isFocused = false;

    public RoundedTextField() {
        this(15);
    }

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        updateBorderPadding();
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public RoundedTextField(String placeholder) {
        this();
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public Icon getPrefixIcon() {
        return prefixIcon;
    }

    public void setPrefixIcon(Icon icon) {
        this.prefixIcon = icon;
        updateBorderPadding();
        repaint();
    }

    private void updateBorderPadding() {
        int left = (prefixIcon != null) ? 38 : 12;
        setBorder(new EmptyBorder(8, left, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        // Border
        g2.setColor(isFocused ? focusColor : borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // Render prefix icon
        if (prefixIcon != null) {
            int iconY = (getHeight() - prefixIcon.getIconHeight()) / 2;
            prefixIcon.paintIcon(this, g2, 10, iconY);
        }

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, getInsets().left, y);
        }
    }
}

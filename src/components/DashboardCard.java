package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardCard extends JPanel {

    private String title;
    private String value;
    private Icon icon;
    private Color color1 = new Color(74, 144, 226);
    private Color color2 = new Color(30, 136, 229);
    private boolean hovered = false;

    public DashboardCard(String title, String value, Icon icon) {
        this.title = title;
        this.value = value;
        this.icon = icon;

        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setLayout(new BorderLayout());

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

        // Labels
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(240, 240, 240));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        add(textPanel, BorderLayout.CENTER);
        
        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);
            add(lblIcon, BorderLayout.EAST);
        }
    }

    public DashboardCard(String title, String value, Icon icon, Color color1, Color color2) {
        this(title, value, icon);
        this.color1 = color1;
        this.color2 = color2;
    }

    public void setValue(String val) {
        this.value = val;
        // Find JLabels inside and update
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                for (Component tc : ((JPanel) c).getComponents()) {
                    if (tc instanceof JLabel) {
                        JLabel lbl = (JLabel) tc;
                        if (lbl.getFont().isBold()) {
                            lbl.setText(val);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Subtly shift color on hover
        Color c1 = hovered ? color1.brighter() : color1;
        Color c2 = hovered ? color2.brighter() : color2;

        GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, hovered ? 0 : 2, w, hovered ? h : h - 2, 16, 16);

        // Add glass reflection overlay
        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillOval(-30, -30, w / 2, h + 60);

        g2.dispose();
        super.paintComponent(g);
    }
}

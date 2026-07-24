package components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable() {
        super();
        initStyle();
    }

    public ModernTable(javax.swing.table.TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(35);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setGridColor(new Color(230, 230, 230));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Alternating row renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(24, 144, 255, 40));
                    c.setForeground(table.getForeground());
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                    c.setForeground(new Color(50, 50, 50));
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        });

        // Style the Table Header
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);
        header.setBackground(new Color(240, 242, 245));
        header.setForeground(new Color(70, 70, 70));
        header.setPreferredSize(new Dimension(100, 40));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBackground(new Color(240, 242, 245));
                label.setForeground(new Color(60, 60, 60));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        };
        header.setDefaultRenderer(headerRenderer);
    }
}

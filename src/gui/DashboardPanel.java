package gui;

import components.DashboardCard;
import service.DashboardManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private DashboardManagement dashboardService = new DashboardManagement();
    private DashboardCard cardTotalBooks;
    private DashboardCard cardAvailableBooks;
    private DashboardCard cardIssuedBooks;
    private DashboardCard cardTotalStudents;
    private PieChartPanel pieChart;
    private BarChartPanel barChart;

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Top Panel: Title
        JLabel lblHeader = new JLabel("Library Analytics Dashboard");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(60, 60, 60));
        add(lblHeader, BorderLayout.NORTH);

        // Center Panel: Cards Grid & Charts
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);

        // 1. Grid of 4 Cards
        JPanel cardGrid = new JPanel(new GridLayout(1, 4, 20, 20));
        cardGrid.setOpaque(false);

        cardTotalBooks = new DashboardCard("Total Books", "0", null, new Color(74, 144, 226), new Color(30, 136, 229));
        cardAvailableBooks = new DashboardCard("Available Books", "0", null, new Color(46, 204, 113), new Color(39, 174, 96));
        cardIssuedBooks = new DashboardCard("Issued Books", "0", null, new Color(241, 196, 15), new Color(243, 156, 18));
        cardTotalStudents = new DashboardCard("Total Students", "0", null, new Color(155, 89, 182), new Color(142, 68, 173));

        cardGrid.add(cardTotalBooks);
        cardGrid.add(cardAvailableBooks);
        cardGrid.add(cardIssuedBooks);
        cardGrid.add(cardTotalStudents);

        mainContent.add(cardGrid, BorderLayout.NORTH);

        // 2. Charts Container
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsContainer.setOpaque(false);

        pieChart = new PieChartPanel();
        barChart = new BarChartPanel();

        chartsContainer.add(pieChart);
        chartsContainer.add(barChart);

        mainContent.add(chartsContainer, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        
        refreshData();
    }

    public void refreshData() {
        // Load data in SwingWorker to prevent blocking UI thread
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() throws Exception {
                int total = dashboardService.getTotalBooks();
                int available = dashboardService.getAvailableBooks();
                int issued = dashboardService.getIssuedBooks();
                int students = dashboardService.getTotalStudents();
                return new int[]{total, available, issued, students};
            }

            @Override
            protected void done() {
                try {
                    int[] data = get();
                    cardTotalBooks.setValue(String.valueOf(data[0]));
                    cardAvailableBooks.setValue(String.valueOf(data[1]));
                    cardIssuedBooks.setValue(String.valueOf(data[2]));
                    cardTotalStudents.setValue(String.valueOf(data[3]));

                    // Update charts
                    pieChart.setData(data[1], data[2]);
                    barChart.setData(data[0], data[1], data[2], data[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Custom Java2D Pie Chart Panel
    private static class PieChartPanel extends JPanel {
        private int available = 0;
        private int issued = 0;

        public PieChartPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(300, 250));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    new EmptyBorder(15, 15, 15, 15)
            ));
        }

        public void setData(int available, int issued) {
            this.available = available;
            this.issued = issued;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw card container
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            // Title
            g2.setColor(new Color(60, 60, 60));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Inventory Ratio", 15, 25);

            int total = available + issued;
            int cx = w / 2 - 60;
            int cy = h / 2 - 60;
            int diameter = 120;

            if (total == 0) {
                g2.setColor(new Color(230, 230, 230));
                g2.fillOval(cx, cy, diameter, diameter);
                g2.setColor(new Color(150, 150, 150));
                g2.drawString("No Data Available", cx + 12, cy + 65);
            } else {
                int angleAvailable = (int) Math.round((double) available / total * 360);
                int angleIssued = 360 - angleAvailable;

                // Draw Available Slice
                g2.setColor(new Color(46, 204, 113)); // Green
                g2.fillArc(cx, cy, diameter, diameter, 0, angleAvailable);

                // Draw Issued Slice
                g2.setColor(new Color(241, 196, 15)); // Orange
                g2.fillArc(cx, cy, diameter, diameter, angleAvailable, angleIssued);
            }

            // Legend
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(46, 204, 113));
            g2.fillRect(w - 120, 60, 12, 12);
            g2.setColor(new Color(80, 80, 80));
            g2.drawString("Available (" + available + ")", w - 100, 71);

            g2.setColor(new Color(241, 196, 15));
            g2.fillRect(w - 120, 85, 12, 12);
            g2.setColor(new Color(80, 80, 80));
            g2.drawString("Issued (" + issued + ")", w - 100, 96);

            g2.dispose();
        }
    }

    // Custom Java2D Bar Chart Panel
    private static class BarChartPanel extends JPanel {
        private int total = 0;
        private int available = 0;
        private int issued = 0;
        private int students = 0;

        public BarChartPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(300, 250));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    new EmptyBorder(15, 15, 15, 15)
            ));
        }

        public void setData(int total, int available, int issued, int students) {
            this.total = total;
            this.available = available;
            this.issued = issued;
            this.students = students;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            // Title
            g2.setColor(new Color(60, 60, 60));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Library Summary", 15, 25);

            int maxVal = Math.max(Math.max(total, available), Math.max(issued, students));
            if (maxVal == 0) maxVal = 10; // Avoid divide by zero

            int chartHeight = h - 100;
            int barWidth = 35;
            int spacing = 35;
            int startX = 40;
            int startY = h - 45;

            int[] values = {total, available, issued, students};
            String[] labels = {"Books", "Avail", "Issued", "Studs"};
            Color[] colors = {
                    new Color(74, 144, 226),
                    new Color(46, 204, 113),
                    new Color(241, 196, 15),
                    new Color(155, 89, 182)
            };

            for (int i = 0; i < 4; i++) {
                int barHeight = (int) Math.round((double) values[i] / maxVal * chartHeight);
                int x = startX + i * (barWidth + spacing);
                int y = startY - barHeight;

                // Draw Bar
                g2.setColor(colors[i]);
                g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                // Draw Value text
                g2.setColor(new Color(80, 80, 80));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(String.valueOf(values[i]), x + (barWidth - g2.getFontMetrics().stringWidth(String.valueOf(values[i]))) / 2, y - 5);

                // Draw Label text
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(labels[i], x + (barWidth - g2.getFontMetrics().stringWidth(labels[i])) / 2, startY + 20);
            }

            g2.dispose();
        }
    }
}

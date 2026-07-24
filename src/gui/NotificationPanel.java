package gui;

import components.*;
import model.Notification;
import service.NotificationManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationPanel extends JPanel {

    private NotificationManagement notificationService = new NotificationManagement();
    private ModernTable table;
    private DefaultTableModel tableModel;

    public NotificationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Alerts & Notifications");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        RoundedButton btnMarkRead = new RoundedButton("Mark All as Read", new Color(46, 204, 113), new Color(39, 174, 96));
        btnMarkRead.addActionListener(e -> markAllAsRead());

        RoundedButton btnDelete = new RoundedButton("Delete Alert", new Color(231, 76, 60), new Color(192, 41, 43));
        btnDelete.addActionListener(e -> deleteSelectedNotification());

        actionsPanel.add(btnMarkRead);
        actionsPanel.add(btnDelete);
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Alert Message", "Received Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new ModernTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        new SwingWorker<List<Notification>, Void>() {
            @Override
            protected List<Notification> doInBackground() throws Exception {
                return notificationService.getAllNotifications();
            }

            @Override
            protected void done() {
                try {
                    List<Notification> list = get();
                    tableModel.setRowCount(0);
                    for (Notification n : list) {
                        tableModel.addRow(new Object[]{
                                n.getNotificationId(),
                                n.getMessage(),
                                n.getNotificationDate(),
                                n.getStatus()
                        });
                    }
                    
                    // Update main header notification counts if visible
                    Frame parent = (Frame) SwingUtilities.getWindowAncestor(NotificationPanel.this);
                    if (parent instanceof MainFrame) {
                        ((MainFrame) parent).updateNotificationCount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void markAllAsRead() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                notificationService.markAllAsRead();
                return null;
            }

            @Override
            protected void done() {
                ToastNotification.show(parent, "All notifications marked as read", ToastNotification.Type.SUCCESS);
                refreshTable();
            }
        }.execute();
    }

    private void deleteSelectedNotification() {
        int row = table.getSelectedRow();
        if (row == -1) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Select an alert to delete", ToastNotification.Type.ERROR);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmationDialog.show(parent, "Delete Alert", "Remove notification alert " + id + "?");
        if (confirmed) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    notificationService.deleteNotification(id);
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Alert deleted successfully", ToastNotification.Type.SUCCESS);
                    refreshTable();
                }
            }.execute();
        }
    }
}

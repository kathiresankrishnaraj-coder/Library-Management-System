package gui;

import components.*;
import model.User;
import service.UserManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private UserManagement userService = new UserManagement();
    private ModernTable table;
    private DefaultTableModel tableModel;

    public UserManagementPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Operator Accounts");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Add Account", new Color(46, 204, 113), new Color(39, 174, 96));
        btnAdd.addActionListener(e -> showUserDialog(null));

        RoundedButton btnUpdateRole = new RoundedButton("Change Role", new Color(241, 196, 15), new Color(243, 156, 18));
        btnUpdateRole.addActionListener(e -> showUpdateRoleDialog());

        RoundedButton btnDelete = new RoundedButton("Delete Account", new Color(231, 76, 60), new Color(192, 41, 43));
        btnDelete.addActionListener(e -> deleteSelectedUser());

        actionsPanel.add(btnUpdateRole);
        actionsPanel.add(btnDelete);
        actionsPanel.add(btnAdd);
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"User ID", "Username", "Role"};
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
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userService.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> list = get();
                    tableModel.setRowCount(0);
                    for (User u : list) {
                        tableModel.addRow(new Object[]{
                                u.getUserId(),
                                u.getUsername(),
                                u.getRole()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void showUserDialog(User user) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Create User Account", true);
        dlg.setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        JLabel lblHeader = new JLabel("Register Operator Profile");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        panel.add(lblHeader, gbc);

        RoundedTextField txtUsername = new RoundedTextField();
        txtUsername.setPlaceholder("Username");
        gbc.gridy = 1;
        panel.add(txtUsername, gbc);

        RoundedPasswordField txtPassword = new RoundedPasswordField();
        txtPassword.setPlaceholder("Password");
        gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "Librarian"});
        cbRole.setPreferredSize(new Dimension(150, 35));
        gbc.gridy = 3;
        panel.add(cbRole, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(220, 220, 220), new Color(200, 200, 200));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dlg.dispose());

        RoundedButton btnSave = new RoundedButton("Create Account", new Color(24, 144, 255), new Color(11, 101, 192));
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String role = (String) cbRole.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                ToastNotification.show(parent, "All fields are required", ToastNotification.Type.ERROR);
                return;
            }

            User u = new User();
            u.setUsername(username);
            u.setPassword(password);
            u.setRole(role);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    userService.addUser(u);
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "User account created successfully!", ToastNotification.Type.SUCCESS);
                    dlg.dispose();
                    refreshTable();
                }
            }.execute();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        gbc.gridy = 4;
        panel.add(btnPanel, gbc);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setSize(330, dlg.getHeight());
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void showUpdateRoleDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Select an account to edit role", ToastNotification.Type.ERROR);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String username = (String) table.getValueAt(row, 1);
        String currentRole = (String) table.getValueAt(row, 2);

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Update User Role", true);
        dlg.setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        JLabel lblHeader = new JLabel("Update Role for " + username);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        panel.add(lblHeader, gbc);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "Librarian"});
        cbRole.setSelectedItem(currentRole);
        cbRole.setPreferredSize(new Dimension(150, 35));
        gbc.gridy = 1;
        panel.add(cbRole, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(220, 220, 220), new Color(200, 200, 200));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dlg.dispose());

        RoundedButton btnSave = new RoundedButton("Save Role", new Color(24, 144, 255), new Color(11, 101, 192));
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> {
            String role = (String) cbRole.getSelectedItem();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    userService.updateRole(id, role);
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Operator role updated!", ToastNotification.Type.SUCCESS);
                    dlg.dispose();
                    refreshTable();
                }
            }.execute();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        gbc.gridy = 2;
        panel.add(btnPanel, gbc);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setSize(330, dlg.getHeight());
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Select an account to remove", ToastNotification.Type.ERROR);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String username = (String) table.getValueAt(row, 1);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        if (username.equalsIgnoreCase("admin")) {
            ToastNotification.show(parent, "Cannot remove protected root 'admin' account", ToastNotification.Type.ERROR);
            return;
        }

        boolean confirmed = ConfirmationDialog.show(parent, "Delete Account", "Are you sure you want to remove account '" + username + "'?");
        if (confirmed) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    userService.deleteUser(id);
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Operator account deleted successfully", ToastNotification.Type.SUCCESS);
                    refreshTable();
                }
            }.execute();
        }
    }
}

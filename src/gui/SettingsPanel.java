package gui;

import components.*;
import theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JLabel lblHeader = new JLabel("Application Preferences & System Tools");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(60, 60, 60));
        add(lblHeader, BorderLayout.NORTH);

        // Settings Form Card
        RoundedPanel mainPanel = new RoundedPanel(16);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.gridx = 0;

        // --- SECTION 1: Theme preferences ---
        gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblThemeHeader = new JLabel("Look & Feel Theme Configuration");
        lblThemeHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThemeHeader.setForeground(new Color(24, 144, 255));
        mainPanel.add(lblThemeHeader, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Aesthetic Mode:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JRadioButton rbLight = new JRadioButton("Light Mode", !ThemeManager.isDark());
        JRadioButton rbDark = new JRadioButton("Dark Mode", ThemeManager.isDark());
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(rbLight);
        themeGroup.add(rbDark);

        JPanel themeRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        themeRadioPanel.setOpaque(false);
        themeRadioPanel.add(rbLight);
        themeRadioPanel.add(rbDark);
        mainPanel.add(themeRadioPanel, gbc);

        // Theme Toggle Action Listeners
        rbLight.addActionListener(e -> ThemeManager.applyTheme(false));
        rbDark.addActionListener(e -> ThemeManager.applyTheme(true));

        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(12, 10, 12, 10);

        // --- SECTION 2: Maintenance Tools ---
        gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblMaintenanceHeader = new JLabel("Database Backup & Disaster Recovery");
        lblMaintenanceHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblMaintenanceHeader.setForeground(new Color(24, 144, 255));
        mainPanel.add(lblMaintenanceHeader, gbc);

        gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Backup Schema:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        RoundedButton btnBackup = new RoundedButton("Backup Database", new Color(46, 204, 113), new Color(39, 174, 96));
        btnBackup.setPreferredSize(new Dimension(180, 35));
        btnBackup.addActionListener(e -> backupDatabase());
        mainPanel.add(btnBackup, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Restore Schema:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        RoundedButton btnRestore = new RoundedButton("Restore Database", new Color(231, 76, 60), new Color(192, 41, 43));
        btnRestore.setPreferredSize(new Dimension(180, 35));
        btnRestore.addActionListener(e -> restoreDatabase());
        mainPanel.add(btnRestore, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void backupDatabase() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQL Backup Destination");
        fileChooser.setSelectedFile(new File("library_backup.sql"));
        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();

            // Execute mysqldump command in background using ProcessBuilder for correct arg passing
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    ProcessBuilder pb = new ProcessBuilder(
                        "mysqldump",
                        "-u", "root",
                        "-proot",
                        "library_management",
                        "-r", dest.getAbsolutePath()   // -r is the correct result-file flag
                    );
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    int code = process.waitFor();
                    return code == 0;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            ToastNotification.show(parent, "Database backup created successfully!", ToastNotification.Type.SUCCESS);
                        } else {
                            ToastNotification.show(parent, "mysqldump failed. Ensure MySQL bin is in PATH.", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastNotification.show(parent, "Backup execution error: " + e.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        }
    }

    private void restoreDatabase() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQL Script to Restore");
        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File source = fileChooser.getSelectedFile();

            boolean confirm = ConfirmationDialog.show(parent, "Restore Database", "Restore action will overwrite all tables. Proceed?");
            if (!confirm) return;

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // Use ProcessBuilder with stdin redirect for correct restore
                    ProcessBuilder pb = new ProcessBuilder(
                        "mysql",
                        "-u", "root",
                        "-proot",
                        "library_management"
                    );
                    pb.redirectInput(source);   // pipe the SQL file as stdin
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    int code = process.waitFor();
                    return code == 0;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            ToastNotification.show(parent, "Database restored successfully!", ToastNotification.Type.SUCCESS);
                        } else {
                            ToastNotification.show(parent, "mysql restore failed. Ensure MySQL bin is in PATH.", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastNotification.show(parent, "Restore execution error: " + e.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        }
    }
}

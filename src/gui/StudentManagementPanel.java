package gui;

import components.*;
import model.Student;
import service.StudentManagement;
import service.IssueBookManagement;
import utils.ExcelExporter;
import utils.PDFExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StudentManagementPanel extends JPanel {

    private StudentManagement studentService = new StudentManagement();
    private IssueBookManagement issueService = new IssueBookManagement();
    private ModernTable table;
    private DefaultTableModel tableModel;
    private RoundedTextField txtSearch;
    
    private JLabel lblDetailsAvatar;
    private JLabel lblDetailsName;
    private JLabel lblDetailsDept;
    private JLabel lblDetailsPhone;
    private JLabel lblDetailsEmail;
    private JLabel lblDetailsBorrowCount;
    private JLabel lblDetailsFine;
    private Student selectedStudent;

    public StudentManagementPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Top Panel: Title & Actions
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Student Directory");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Add Student", new Color(46, 204, 113), new Color(39, 174, 96));
        btnAdd.addActionListener(e -> showStudentDialog(null));

        RoundedButton btnExport = new RoundedButton("Export CSV", new Color(24, 144, 255), new Color(11, 101, 192));
        btnExport.addActionListener(e -> exportCSV());

        RoundedButton btnPrint = new RoundedButton("Print", new Color(155, 89, 182), new Color(142, 68, 173));
        btnPrint.addActionListener(e -> printTable());

        actionsPanel.add(btnExport);
        actionsPanel.add(btnPrint);
        actionsPanel.add(btnAdd);

        topPanel.add(actionsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Central Split Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(700);

        // Left Side: Table & Search
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setOpaque(false);

        // Search Panel
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        txtSearch = new RoundedTextField();
        txtSearch.setPlaceholder("Search students by name, department, phone...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearch(); }
        });
        searchBar.add(new JLabel("Quick Search:"), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        leftPanel.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Student ID", "Name", "Department", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new ModernTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int id = (int) table.getValueAt(row, 0);
                    loadStudentDetails(id);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Side: Student Profile Details Card
        RoundedPanel rightPanel = new RoundedPanel(16);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        rightPanel.setLayout(new BorderLayout(15, 15));

        JPanel profileDetailsPanel = new JPanel(new GridBagLayout());
        profileDetailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Avatar
        lblDetailsAvatar = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 243, 250));
                g2.fillOval(x, y, 64, 64);
                g2.setColor(new Color(24, 144, 255));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                String initials = selectedStudent == null ? "?" : selectedStudent.getName().substring(0, Math.min(selectedStudent.getName().length(), 2)).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, x + (64 - fm.stringWidth(initials)) / 2, y + (64 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
            @Override public int getIconWidth() { return 64; }
            @Override public int getIconHeight() { return 64; }
        });
        lblDetailsAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        profileDetailsPanel.add(lblDetailsAvatar, gbc);

        lblDetailsName = new JLabel("Select a student", SwingConstants.CENTER);
        lblDetailsName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailsName.setForeground(new Color(50, 50, 50));
        gbc.gridy = 1;
        profileDetailsPanel.add(lblDetailsName, gbc);

        lblDetailsDept = new JLabel("Department: -", SwingConstants.CENTER);
        lblDetailsDept.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetailsDept.setForeground(new Color(120, 120, 120));
        gbc.gridy = 2;
        profileDetailsPanel.add(lblDetailsDept, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        profileDetailsPanel.add(sep, gbc);
        gbc.insets = new Insets(6, 0, 6, 0);

        lblDetailsPhone = new JLabel("Phone: -");
        lblDetailsPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailsPhone.setForeground(new Color(80, 80, 80));
        gbc.gridy = 4;
        profileDetailsPanel.add(lblDetailsPhone, gbc);

        lblDetailsEmail = new JLabel("Email: -");
        lblDetailsEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailsEmail.setForeground(new Color(80, 80, 80));
        gbc.gridy = 5;
        profileDetailsPanel.add(lblDetailsEmail, gbc);

        lblDetailsBorrowCount = new JLabel("Books Issued: -");
        lblDetailsBorrowCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailsBorrowCount.setForeground(new Color(80, 80, 80));
        gbc.gridy = 6;
        profileDetailsPanel.add(lblDetailsBorrowCount, gbc);

        lblDetailsFine = new JLabel("Outstanding Fine: -");
        lblDetailsFine.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailsFine.setForeground(Color.GRAY);
        gbc.gridy = 7;
        profileDetailsPanel.add(lblDetailsFine, gbc);

        rightPanel.add(profileDetailsPanel, BorderLayout.CENTER);

        // Edit/Delete buttons at bottom
        JPanel rightButtons = new JPanel(new GridLayout(2, 1, 10, 10));
        rightButtons.setOpaque(false);
        RoundedButton btnEdit = new RoundedButton("Edit profile", new Color(241, 196, 15), new Color(243, 156, 18));
        btnEdit.addActionListener(e -> {
            if (selectedStudent != null) showStudentDialog(selectedStudent);
            else ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "No student selected", ToastNotification.Type.ERROR);
        });

        RoundedButton btnDelete = new RoundedButton("Remove Student", new Color(231, 76, 60), new Color(192, 41, 43));
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        rightButtons.add(btnEdit);
        rightButtons.add(btnDelete);
        rightPanel.add(rightButtons, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentService.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    List<Student> list = get();
                    tableModel.setRowCount(0);
                    for (Student s : list) {
                        tableModel.addRow(new Object[]{
                                s.getStudentId(),
                                s.getName(),
                                s.getDepartment(),
                                s.getPhone()
                        });
                    }
                    clearDetails();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void performSearch() {
        String search = txtSearch.getText().trim();
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                if (search.isEmpty()) {
                    return studentService.getAllStudents();
                } else {
                    return studentService.searchStudents(search);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Student> list = get();
                    tableModel.setRowCount(0);
                    for (Student s : list) {
                        tableModel.addRow(new Object[]{
                                s.getStudentId(),
                                s.getName(),
                                s.getDepartment(),
                                s.getPhone()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadStudentDetails(int id) {
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                Student s = studentService.getStudentById(id);
                int count = issueService.getBorrowCountForStudent(id);
                double fine = issueService.getOutstandingFinesForStudent(id);
                return new Object[]{s, count, fine};
            }

            @Override
            protected void done() {
                try {
                    Object[] res = get();
                    selectedStudent = (Student) res[0];
                    int count = (int) res[1];
                    double fine = (double) res[2];

                    if (selectedStudent != null) {
                        lblDetailsName.setText(selectedStudent.getName());
                        lblDetailsDept.setText("Department: " + selectedStudent.getDepartment());
                        lblDetailsPhone.setText("Phone: " + selectedStudent.getPhone());
                        lblDetailsEmail.setText("Email: " + selectedStudent.getName().toLowerCase().replace(" ", "") + "@university.edu");
                        lblDetailsBorrowCount.setText("Active Checked-out Books: " + count + " / 3");
                        lblDetailsFine.setText("Outstanding Fine: ₹" + fine);
                        
                        if (fine > 0) {
                            lblDetailsFine.setForeground(new Color(231, 76, 60));
                        } else {
                            lblDetailsFine.setForeground(new Color(39, 174, 96));
                        }
                        lblDetailsAvatar.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void clearDetails() {
        selectedStudent = null;
        lblDetailsName.setText("Select a student");
        lblDetailsDept.setText("Department: -");
        lblDetailsPhone.setText("Phone: -");
        lblDetailsEmail.setText("Email: -");
        lblDetailsBorrowCount.setText("Books Issued: -");
        lblDetailsFine.setText("Outstanding Fine: -");
        lblDetailsFine.setForeground(Color.GRAY);
        lblDetailsAvatar.repaint();
    }

    private void showStudentDialog(Student student) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, student == null ? "Add New Student" : "Edit Student Details", true);
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

        JLabel lblHeader = new JLabel(student == null ? "Register New Student" : "Edit Student Profile");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        panel.add(lblHeader, gbc);

        RoundedTextField txtName = new RoundedTextField();
        txtName.setPlaceholder("Student Full Name");
        gbc.gridy = 1;
        panel.add(txtName, gbc);

        RoundedTextField txtDept = new RoundedTextField();
        txtDept.setPlaceholder("Department");
        gbc.gridy = 2;
        panel.add(txtDept, gbc);

        RoundedTextField txtPhone = new RoundedTextField();
        txtPhone.setPlaceholder("Phone Number");
        gbc.gridy = 3;
        panel.add(txtPhone, gbc);

        if (student != null) {
            txtName.setText(student.getName());
            txtDept.setText(student.getDepartment());
            txtPhone.setText(student.getPhone());
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(220, 220, 220), new Color(200, 200, 200));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dlg.dispose());

        RoundedButton btnSave = new RoundedButton("Save", new Color(24, 144, 255), new Color(11, 101, 192));
        btnSave.setPreferredSize(new Dimension(90, 35));
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String dept = txtDept.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || dept.isEmpty() || phone.isEmpty()) {
                ToastNotification.show(parent, "All fields are required", ToastNotification.Type.ERROR);
                return;
            }

            Student s = student == null ? new Student() : student;
            s.setName(name);
            s.setDepartment(dept);
            s.setPhone(phone);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (student == null) {
                        studentService.addStudent(s);
                    } else {
                        studentService.updateStudent(s);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, student == null ? "Student registered successfully" : "Student profile updated successfully", ToastNotification.Type.SUCCESS);
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
        dlg.setSize(360, dlg.getHeight());
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void deleteSelectedStudent() {
        if (selectedStudent == null) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "No student selected", ToastNotification.Type.ERROR);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmationDialog.show(parent, "Remove Student", "Are you sure you want to remove '" + selectedStudent.getName() + "'?");
        if (confirmed) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    studentService.deleteStudent(selectedStudent.getStudentId());
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Student record deleted successfully", ToastNotification.Type.SUCCESS);
                    refreshTable();
                }
            }.execute();
        }
    }

    private void exportCSV() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Students List");
        fileChooser.setSelectedFile(new File("Students_Report.csv"));
        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (ExcelExporter.exportToCSV(table, file)) {
                ToastNotification.show(parent, "Data exported successfully", ToastNotification.Type.SUCCESS);
            } else {
                ToastNotification.show(parent, "Export failed", ToastNotification.Type.ERROR);
            }
        }
    }

    private void printTable() {
        PDFExporter.printTable(table, "Library Students Directory Report", "LMS ERP System");
    }
}

package gui;

import components.*;
import model.Book;
import model.Student;
import model.IssueBook;
import service.BookManagement;
import service.StudentManagement;
import service.IssueBookManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IssueBookPanel extends JPanel {

    private BookManagement bookService = new BookManagement();
    private StudentManagement studentService = new StudentManagement();
    private IssueBookManagement issueService = new IssueBookManagement();

    private RoundedTextField txtBookId;
    private RoundedTextField txtStudentId;
    private RoundedTextField txtIssueDate;
    private RoundedTextField txtReturnDate;

    // Info Labels
    private JLabel lblBookInfo;
    private JLabel lblStudentInfo;
    private JLabel lblBorrowCount;
    private JLabel lblStockCount;
    private JLabel lblDurationInfo;

    private Book verifiedBook = null;
    private Student verifiedStudent = null;

    private RoundedButton btnIssue;

    public IssueBookPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Top Panel: Header
        JLabel lblHeader = new JLabel("Issue Book Transaction");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(60, 60, 60));
        add(lblHeader, BorderLayout.NORTH);

        // Center Panel: Form
        RoundedPanel formPanel = new RoundedPanel(16);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);

        // --- SECTION 1: Student Lookup ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Student ID:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.6;
        txtStudentId = new RoundedTextField();
        txtStudentId.setPlaceholder("Enter Student ID");
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        RoundedButton btnVerifyStudent = new RoundedButton("Verify Student", new Color(24, 144, 255), new Color(11, 101, 192));
        btnVerifyStudent.setPreferredSize(new Dimension(130, 35));
        btnVerifyStudent.addActionListener(e -> verifyStudent());
        formPanel.add(btnVerifyStudent, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        lblStudentInfo = new JLabel("No student verified yet.");
        lblStudentInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblStudentInfo.setForeground(Color.GRAY);
        formPanel.add(lblStudentInfo, gbc);

        gbc.gridy = 2;
        lblBorrowCount = new JLabel("");
        lblBorrowCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblBorrowCount, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 20, 10);
        formPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(12, 10, 12, 10);

        // --- SECTION 2: Book Lookup ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtBookId = new RoundedTextField();
        txtBookId.setPlaceholder("Enter Book ID");
        formPanel.add(txtBookId, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        RoundedButton btnVerifyBook = new RoundedButton("Verify Book", new Color(24, 144, 255), new Color(11, 101, 192));
        btnVerifyBook.setPreferredSize(new Dimension(130, 35));
        btnVerifyBook.addActionListener(e -> verifyBook());
        formPanel.add(btnVerifyBook, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2;
        lblBookInfo = new JLabel("No book verified yet.");
        lblBookInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblBookInfo.setForeground(Color.GRAY);
        formPanel.add(lblBookInfo, gbc);

        gbc.gridy = 6;
        lblStockCount = new JLabel("");
        lblStockCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblStockCount, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 20, 10);
        formPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(12, 10, 12, 10);

        // --- SECTION 3: Dates and Submit ---
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Issue Date:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.9;
        txtIssueDate = new RoundedTextField();
        formPanel.add(txtIssueDate, gbc);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 1; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Return Due:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.9;
        txtReturnDate = new RoundedTextField();
        formPanel.add(txtReturnDate, gbc);

        gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 2;
        lblDurationInfo = new JLabel("Borrowed Period: 14 days");
        lblDurationInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDurationInfo.setForeground(new Color(39, 174, 96));
        formPanel.add(lblDurationInfo, gbc);

        // Document listeners for live date validation and calculation
        javax.swing.event.DocumentListener dateChangeListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateDuration(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateDuration(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateDuration(); }
        };
        txtIssueDate.getDocument().addDocumentListener(dateChangeListener);
        txtReturnDate.getDocument().addDocumentListener(dateChangeListener);

        // Setup Dates defaults (Today and Today + 14 days)
        setupDates();

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 0, 10);
        btnIssue = new RoundedButton("Execute Book Issue", new Color(46, 204, 113), new Color(39, 174, 96));
        btnIssue.setPreferredSize(new Dimension(200, 45));
        btnIssue.addActionListener(e -> executeIssue());
        formPanel.add(btnIssue, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void setupDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        txtIssueDate.setText(sdf.format(cal.getTime()));
        
        cal.add(Calendar.DATE, 14); // 2 weeks default duration
        txtReturnDate.setText(sdf.format(cal.getTime()));
        calculateDuration();
    }

    private void calculateDuration() {
        if (txtIssueDate == null || txtReturnDate == null || lblDurationInfo == null) return;
        String issueStr = txtIssueDate.getText().trim();
        String returnStr = txtReturnDate.getText().trim();

        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate issueDate = java.time.LocalDate.parse(issueStr);
            java.time.LocalDate returnDate = java.time.LocalDate.parse(returnStr);

            if (issueDate.isBefore(today)) {
                lblDurationInfo.setText("❌ Error: Issue date cannot be earlier than today's date (" + today + ")");
                lblDurationInfo.setForeground(new Color(231, 76, 60));
            } else if (returnDate.isBefore(issueDate)) {
                lblDurationInfo.setText("❌ Error: Return date cannot be earlier than issue date!");
                lblDurationInfo.setForeground(new Color(231, 76, 60));
            } else {
                long days = java.time.temporal.ChronoUnit.DAYS.between(issueDate, returnDate);
                lblDurationInfo.setText("✓ Borrowed Period: " + days + " days");
                lblDurationInfo.setForeground(new Color(39, 174, 96));
            }
        } catch (Exception ex) {
            lblDurationInfo.setText("Enter valid dates (YYYY-MM-DD)");
            lblDurationInfo.setForeground(Color.GRAY);
        }
    }

    private void verifyStudent() {
        String idStr = txtStudentId.getText().trim();
        if (idStr.isEmpty()) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Enter student ID first", ToastNotification.Type.ERROR);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    Student s = studentService.getStudentById(id);
                    int count = issueService.getBorrowCountForStudent(id);
                    return new Object[]{s, count};
                }

                @Override
                protected void done() {
                    try {
                        Object[] res = get();
                        verifiedStudent = (Student) res[0];
                        int count = (int) res[1];

                        if (verifiedStudent != null) {
                            lblStudentInfo.setText("Student Name: " + verifiedStudent.getName() + " | Dept: " + verifiedStudent.getDepartment());
                            lblStudentInfo.setForeground(new Color(39, 174, 96));
                            
                            lblBorrowCount.setText("Active Checkouts: " + count + " / 3 books");
                            if (count >= 3) {
                                lblBorrowCount.setForeground(new Color(231, 76, 60));
                                ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(IssueBookPanel.this), "Borrow limit reached (max 3 books)!", ToastNotification.Type.ERROR);
                            } else {
                                lblBorrowCount.setForeground(new Color(46, 204, 113));
                            }
                        } else {
                            verifiedStudent = null;
                            lblStudentInfo.setText("Student ID not found in database.");
                            lblStudentInfo.setForeground(new Color(231, 76, 60));
                            lblBorrowCount.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Student ID must be numeric", ToastNotification.Type.ERROR);
        }
    }

    private void verifyBook() {
        String idStr = txtBookId.getText().trim();
        if (idStr.isEmpty()) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Enter Book ID first", ToastNotification.Type.ERROR);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            new SwingWorker<Book, Void>() {
                @Override
                protected Book doInBackground() throws Exception {
                    return bookService.getBookById(id);
                }

                @Override
                protected void done() {
                    try {
                        verifiedBook = get();
                        if (verifiedBook != null) {
                            lblBookInfo.setText("Title: " + verifiedBook.getTitle() + " | Author: " + verifiedBook.getAuthor());
                            lblBookInfo.setForeground(new Color(39, 174, 96));

                            lblStockCount.setText("Available stock: " + verifiedBook.getQuantity() + " copies");
                            if (verifiedBook.getQuantity() <= 0) {
                                lblStockCount.setForeground(new Color(231, 76, 60));
                                ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(IssueBookPanel.this), "Book is out of stock!", ToastNotification.Type.ERROR);
                            } else {
                                lblStockCount.setForeground(new Color(46, 204, 113));
                            }
                        } else {
                            verifiedBook = null;
                            lblBookInfo.setText("Book ID not found in database.");
                            lblBookInfo.setForeground(new Color(231, 76, 60));
                            lblStockCount.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Book ID must be numeric", ToastNotification.Type.ERROR);
        }
    }

    private void executeIssue() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        if (verifiedStudent == null) {
            ToastNotification.show(parent, "Please verify student ID first", ToastNotification.Type.ERROR);
            return;
        }

        if (verifiedBook == null) {
            ToastNotification.show(parent, "Please verify book ID first", ToastNotification.Type.ERROR);
            return;
        }

        if (verifiedBook.getQuantity() <= 0) {
            ToastNotification.show(parent, "Book copy unavailable (out of stock)", ToastNotification.Type.ERROR);
            return;
        }

        String issueDateStr = txtIssueDate.getText().trim();
        String returnDateStr = txtReturnDate.getText().trim();

        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate issueLocalDate = java.time.LocalDate.parse(issueDateStr);
            java.time.LocalDate returnLocalDate = java.time.LocalDate.parse(returnDateStr);

            if (issueLocalDate.isBefore(today)) {
                JOptionPane.showMessageDialog(parent, 
                    "Invalid Borrow Date!\nThe issue date cannot be earlier than today's system date (" + today + ").", 
                    "Date Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                ToastNotification.show(parent, "Issue date cannot be earlier than today's date", ToastNotification.Type.ERROR);
                return;
            }

            if (returnLocalDate.isBefore(issueLocalDate)) {
                JOptionPane.showMessageDialog(parent, 
                    "Invalid Return Date!\nThe return date must never be earlier than the issue date.", 
                    "Date Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                ToastNotification.show(parent, "Return date cannot be earlier than issue date", ToastNotification.Type.ERROR);
                return;
            }

            Date issueDate = Date.valueOf(issueLocalDate);
            Date returnDate = Date.valueOf(returnLocalDate);

            // Fetch current count directly to avoid race conditions
            new SwingWorker<Boolean, Void>() {
                private int activeCount = 0;
                
                @Override
                protected Boolean doInBackground() throws Exception {
                    activeCount = issueService.getBorrowCountForStudent(verifiedStudent.getStudentId());
                    if (activeCount >= 3) {
                        return false;
                    }

                    IssueBook issue = new IssueBook();
                    issue.setBookId(verifiedBook.getBookId());
                    issue.setStudentId(verifiedStudent.getStudentId());
                    issue.setIssueDate(issueDate);
                    issue.setReturnDate(returnDate);
                    issue.setStatus("Issued");

                    issueService.issueBook(issue);
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            ToastNotification.show(parent, "Book issued successfully!", ToastNotification.Type.SUCCESS);
                            clearForm();
                        } else {
                            ToastNotification.show(parent, "Failed: Student limit exceeded (max 3 books)!", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastNotification.show(parent, "Error issuing book: Check database constraint", ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid Date format! Please use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
            ToastNotification.show(parent, "Invalid Date format. Use YYYY-MM-DD", ToastNotification.Type.ERROR);
        }
    }

    private void clearForm() {
        txtBookId.setText("");
        txtStudentId.setText("");
        lblBookInfo.setText("No book verified yet.");
        lblBookInfo.setForeground(Color.GRAY);
        lblStockCount.setText("");
        lblStudentInfo.setText("No student verified yet.");
        lblStudentInfo.setForeground(Color.GRAY);
        lblBorrowCount.setText("");
        verifiedBook = null;
        verifiedStudent = null;
        setupDates();
    }
}

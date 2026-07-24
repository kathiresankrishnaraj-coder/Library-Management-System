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

public class ReturnBookPanel extends JPanel {

    private BookManagement bookService = new BookManagement();
    private StudentManagement studentService = new StudentManagement();
    private IssueBookManagement issueService = new IssueBookManagement();

    private RoundedTextField txtBookId;
    private RoundedTextField txtStudentId;

    private JLabel lblBookTitle;
    private JLabel lblStudentName;
    private JLabel lblIssueDate;
    private JLabel lblDueDate;
    private JLabel lblTotalBorrowedDays;
    private JLabel lblOverdueDays;
    private JLabel lblFine;

    private IssueBook activeIssue = null;
    private Book activeBook = null;
    private Student activeStudent = null;

    private RoundedButton btnReturn;

    public ReturnBookPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JLabel lblHeader = new JLabel("Return Book & Calculate Fine");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(60, 60, 60));
        add(lblHeader, BorderLayout.NORTH);

        // Content panel
        RoundedPanel mainPanel = new RoundedPanel(16);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);

        // Inputs
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        mainPanel.add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtBookId = new RoundedTextField();
        txtBookId.setPlaceholder("Enter Book ID");
        mainPanel.add(txtBookId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        mainPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        txtStudentId = new RoundedTextField();
        txtStudentId.setPlaceholder("Enter Student ID");
        mainPanel.add(txtStudentId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0.3;
        RoundedButton btnVerify = new RoundedButton("Fetch Issue Record", new Color(24, 144, 255), new Color(11, 101, 192));
        btnVerify.setPreferredSize(new Dimension(150, 65));
        btnVerify.addActionListener(e -> fetchIssueRecord());
        mainPanel.add(btnVerify, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.gridheight = 1;
        gbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(12, 10, 12, 10);

        // Details Display Panel
        JPanel detailsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        detailsPanel.setOpaque(false);

        lblBookTitle = new JLabel("-");
        lblStudentName = new JLabel("-");
        lblIssueDate = new JLabel("-");
        lblDueDate = new JLabel("-");
        lblTotalBorrowedDays = new JLabel("-");
        lblOverdueDays = new JLabel("-");
        lblFine = new JLabel("-");

        Font detailFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valFont = new Font("Segoe UI", Font.BOLD, 14);

        detailsPanel.add(createLabel("Book Title:", detailFont));
        detailsPanel.add(lblBookTitle);
        lblBookTitle.setFont(valFont);

        detailsPanel.add(createLabel("Student Name:", detailFont));
        detailsPanel.add(lblStudentName);
        lblStudentName.setFont(valFont);

        detailsPanel.add(createLabel("Issue Date:", detailFont));
        detailsPanel.add(lblIssueDate);
        lblIssueDate.setFont(valFont);

        detailsPanel.add(createLabel("Due Return Date:", detailFont));
        detailsPanel.add(lblDueDate);
        lblDueDate.setFont(valFont);

        detailsPanel.add(createLabel("Total Borrowed Days:", detailFont));
        detailsPanel.add(lblTotalBorrowedDays);
        lblTotalBorrowedDays.setFont(valFont);

        detailsPanel.add(createLabel("Days Overdue:", detailFont));
        detailsPanel.add(lblOverdueDays);
        lblOverdueDays.setFont(valFont);

        detailsPanel.add(createLabel("Fine Assessed (₹5/day):", detailFont));
        detailsPanel.add(lblFine);
        lblFine.setFont(new Font("Segoe UI", Font.BOLD, 16));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        mainPanel.add(detailsPanel, gbc);

        // Submit Button
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 10, 0, 10);
        btnReturn = new RoundedButton("Process Return", new Color(46, 204, 113), new Color(39, 174, 96));
        btnReturn.setPreferredSize(new Dimension(200, 45));
        btnReturn.addActionListener(e -> executeReturn());
        btnReturn.setEnabled(false);
        mainPanel.add(btnReturn, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(100, 100, 100));
        return label;
    }

    private void fetchIssueRecord() {
        String bookIdStr = txtBookId.getText().trim();
        String studentIdStr = txtStudentId.getText().trim();

        if (bookIdStr.isEmpty() || studentIdStr.isEmpty()) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Both Book ID and Student ID are required", ToastNotification.Type.ERROR);
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            int studentId = Integer.parseInt(studentIdStr);

            new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    IssueBook issue = issueService.getActiveIssue(bookId, studentId);
                    if (issue == null) {
                        return null;
                    }
                    Book b = bookService.getBookById(bookId);
                    Student s = studentService.getStudentById(studentId);
                    return new Object[]{issue, b, s};
                }

                @Override
                protected void done() {
                    try {
                        Object[] res = get();
                        if (res == null) {
                            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(ReturnBookPanel.this), "No active checkout record found!", ToastNotification.Type.ERROR);
                            clearDetails();
                            return;
                        }

                        activeIssue = (IssueBook) res[0];
                        activeBook = (Book) res[1];
                        activeStudent = (Student) res[2];

                        lblBookTitle.setText(activeBook.getTitle());
                        lblStudentName.setText(activeStudent.getName());
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        lblIssueDate.setText(sdf.format(activeIssue.getIssueDate()));
                        lblDueDate.setText(sdf.format(activeIssue.getReturnDate()));

                        // Calculate total borrowed days and fine
                        java.sql.Date issueDate = activeIssue.getIssueDate();
                        java.sql.Date dueDate = activeIssue.getReturnDate();
                        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                        long totalDaysDiff = today.getTime() - issueDate.getTime();
                        long totalDays = Math.max(0, totalDaysDiff / (1000 * 60 * 60 * 24));
                        lblTotalBorrowedDays.setText(totalDays + " days");

                        long diff = today.getTime() - dueDate.getTime();
                        long lateDays = diff / (1000 * 60 * 60 * 24);
                        if (lateDays < 0) {
                            lateDays = 0;
                        }
                        double fine = lateDays * 5.0;

                        lblOverdueDays.setText(lateDays + " days");
                        lblFine.setText("₹" + fine);
                        
                        if (fine > 0) {
                            lblFine.setForeground(new Color(231, 76, 60));
                            lblOverdueDays.setForeground(new Color(231, 76, 60));
                        } else {
                            lblFine.setForeground(new Color(39, 174, 96));
                            lblOverdueDays.setForeground(new Color(39, 174, 96));
                        }

                        btnReturn.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "IDs must be numeric integers", ToastNotification.Type.ERROR);
        }
    }

    private void executeReturn() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        if (activeIssue == null) return;

        boolean confirm = ConfirmationDialog.show(parent, "Confirm Return", "Process check-in and update book stock?");
        if (confirm) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    issueService.returnBook(activeIssue.getBookId(), activeIssue.getStudentId());
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Book checked-in and stock updated!", ToastNotification.Type.SUCCESS);
                    clearForm();
                }
            }.execute();
        }
    }

    private void clearDetails() {
        activeIssue = null;
        activeBook = null;
        activeStudent = null;
        lblBookTitle.setText("-");
        lblStudentName.setText("-");
        lblIssueDate.setText("-");
        lblDueDate.setText("-");
        lblTotalBorrowedDays.setText("-");
        lblOverdueDays.setText("-");
        lblFine.setText("-");
        lblFine.setForeground(Color.BLACK);
        lblOverdueDays.setForeground(Color.BLACK);
        btnReturn.setEnabled(false);
    }

    private void clearForm() {
        txtBookId.setText("");
        txtStudentId.setText("");
        clearDetails();
    }
}

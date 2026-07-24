package gui;

import components.*;
import model.*;
import service.*;
import utils.ExcelExporter;
import utils.PDFExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReportsPanel extends JPanel {

    private BookManagement bookService = new BookManagement();
    private StudentManagement studentService = new StudentManagement();
    private IssueBookManagement issueService = new IssueBookManagement();
    private ReservationManagement reservationService = new ReservationManagement();
    private PurchaseManagement purchaseService = new PurchaseManagement();

    private JComboBox<String> cbCategory;
    private ModernTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public ReportsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Reports & Analytics Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Date filter, category and export buttons
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Select Dataset:"));
        
        String[] categories = {"Inventory Summary", "Acquisitions Report", "Checked-Out Books", "Overdue Books", "Reservations waitlist", "Students Directory"};
        cbCategory = new JComboBox<>(categories);
        cbCategory.setPreferredSize(new Dimension(180, 35));
        cbCategory.addActionListener(e -> generateReport());
        filterPanel.add(cbCategory);

        RoundedButton btnExport = new RoundedButton("Export CSV", new Color(24, 144, 255), new Color(11, 101, 192));
        btnExport.addActionListener(e -> exportCSV());

        RoundedButton btnPrint = new RoundedButton("Print / PDF", new Color(155, 89, 182), new Color(142, 68, 173));
        btnPrint.addActionListener(e -> printReport());

        filterPanel.add(btnExport);
        filterPanel.add(btnPrint);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel();
        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        // Status bar panel
        lblStatus = new JLabel("Ready. Choose a report dataset from the dropdown.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblStatus.setForeground(Color.GRAY);
        add(lblStatus, BorderLayout.SOUTH);

        generateReport();
    }

    private void generateReport() {
        int idx = cbCategory.getSelectedIndex();
        String selected = (String) cbCategory.getSelectedItem();
        lblStatus.setText("Generating report for " + selected + "...");

        new SwingWorker<Object[][], Void>() {
            private String[] columns;

            @Override
            protected Object[][] doInBackground() throws Exception {
                switch (idx) {
                    case 0: { // Inventory Summary
                        columns = new String[]{"Book ID", "Title", "Author", "Category", "Quantity", "Price"};
                        List<Book> books = bookService.getAllBooks();
                        Object[][] data = new Object[books.size()][6];
                        for (int i = 0; i < books.size(); i++) {
                            Book b = books.get(i);
                            data[i] = new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getQuantity(), "₹" + b.getPrice()};
                        }
                        return data;
                    }
                    case 1: { // Acquisitions Report
                        columns = new String[]{"Purchase ID", "Book Title", "Supplier", "Quantity Purchased", "Unit Price", "Total Cost", "Date"};
                        List<Purchase> purchases = purchaseService.getAllPurchases();
                        Object[][] data = new Object[purchases.size()][7];
                        for (int i = 0; i < purchases.size(); i++) {
                            Purchase p = purchases.get(i);
                            data[i] = new Object[]{p.getPurchaseId(), p.getBookTitle(), p.getSupplierName(), p.getQuantity(), "₹" + p.getPricePerBook(), "₹" + p.getTotalAmount(), p.getPurchaseDate()};
                        }
                        return data;
                    }
                    case 2: { // Checked-Out Books
                        columns = new String[]{"Issue ID", "Book ID", "Student ID", "Issue Date", "Due Date", "Status", "Fines"};
                        List<IssueBook> issues = issueService.getAllIssuedBooks();
                        Object[][] data = new Object[issues.size()][7];
                        for (int i = 0; i < issues.size(); i++) {
                            IssueBook ib = issues.get(i);
                            data[i] = new Object[]{ib.getIssueId(), ib.getBookId(), ib.getStudentId(), ib.getIssueDate(), ib.getReturnDate(), ib.getStatus(), "₹" + ib.getFine()};
                        }
                        return data;
                    }
                    case 3: { // Overdue Books
                        columns = new String[]{"Issue ID", "Book ID", "Student ID", "Checked-Out On", "Due Date", "Status", "Fines"};
                        List<IssueBook> issues = issueService.getOverdueBooks();
                        Object[][] data = new Object[issues.size()][7];
                        for (int i = 0; i < issues.size(); i++) {
                            IssueBook ib = issues.get(i);
                            data[i] = new Object[]{ib.getIssueId(), ib.getBookId(), ib.getStudentId(), ib.getIssueDate(), ib.getReturnDate(), ib.getStatus(), "₹" + ib.getFine()};
                        }
                        return data;
                    }
                    case 4: { // Reservations waitlist
                        columns = new String[]{"Reservation ID", "Student Name", "Book Title", "Reservation Date", "Status"};
                        List<Reservation> list = reservationService.getAllReservations();
                        Object[][] data = new Object[list.size()][5];
                        for (int i = 0; i < list.size(); i++) {
                            Reservation r = list.get(i);
                            data[i] = new Object[]{r.getReservationId(), r.getStudentName(), r.getBookTitle(), r.getReservationDate(), r.getStatus()};
                        }
                        return data;
                    }
                    case 5: { // Students Directory
                        columns = new String[]{"Student ID", "Name", "Department", "Phone"};
                        List<Student> list = studentService.getAllStudents();
                        Object[][] data = new Object[list.size()][4];
                        for (int i = 0; i < list.size(); i++) {
                            Student s = list.get(i);
                            data[i] = new Object[]{s.getStudentId(), s.getName(), s.getDepartment(), s.getPhone()};
                        }
                        return data;
                    }
                    default:
                        columns = new String[]{};
                        return new Object[][]{};
                }
            }

            @Override
            protected void done() {
                try {
                    Object[][] data = get();
                    // Atomically replace columns and rows on the EDT — no race condition
                    tableModel.setDataVector(data, columns);
                    lblStatus.setText("Report generated: " + selected + " | Total records: " + table.getRowCount());
                } catch (Exception e) {
                    e.printStackTrace();
                    lblStatus.setText("Failed to generate report.");
                }
            }
        }.execute();
    }

    private void exportCSV() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new File("LMS_Report_" + cbCategory.getSelectedItem().toString().replace(" ", "_") + ".csv"));
        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (ExcelExporter.exportToCSV(table, file)) {
                ToastNotification.show(parent, "Report exported successfully", ToastNotification.Type.SUCCESS);
            } else {
                ToastNotification.show(parent, "Export failed", ToastNotification.Type.ERROR);
            }
        }
    }

    private void printReport() {
        PDFExporter.printTable(table, cbCategory.getSelectedItem().toString() + " - LMS ERP", "Academics Library System");
    }
}

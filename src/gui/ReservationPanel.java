package gui;

import components.*;
import model.Reservation;
import model.Book;
import model.Student;
import service.ReservationManagement;
import service.BookManagement;
import service.StudentManagement;
import utils.ExcelExporter;
import utils.PDFExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.util.List;

public class ReservationPanel extends JPanel {

    private ReservationManagement reservationService = new ReservationManagement();
    private BookManagement bookService = new BookManagement();
    private StudentManagement studentService = new StudentManagement();

    private ModernTable table;
    private DefaultTableModel tableModel;

    public ReservationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Book Reservations Queue");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("New Reservation", new Color(46, 204, 113), new Color(39, 174, 96));
        btnAdd.addActionListener(e -> showReservationDialog());

        RoundedButton btnCancel = new RoundedButton("Cancel Waitlist", new Color(231, 76, 60), new Color(192, 41, 43));
        btnCancel.addActionListener(e -> cancelSelectedReservation());

        RoundedButton btnExport = new RoundedButton("Export CSV", new Color(24, 144, 255), new Color(11, 101, 192));
        btnExport.addActionListener(e -> exportCSV());

        actionsPanel.add(btnExport);
        actionsPanel.add(btnCancel);
        actionsPanel.add(btnAdd);
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Reservations Table
        String[] columns = {"Reservation ID", "Student Name", "Book Title", "Reservation Date", "Status"};
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
        new SwingWorker<List<Reservation>, Void>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return reservationService.getAllReservations();
            }

            @Override
            protected void done() {
                try {
                    List<Reservation> list = get();
                    tableModel.setRowCount(0);
                    for (Reservation r : list) {
                        tableModel.addRow(new Object[]{
                                r.getReservationId(),
                                r.getStudentName(),
                                r.getBookTitle(),
                                r.getReservationDate(),
                                r.getStatus()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void showReservationDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Create Book Reservation", true);
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

        JLabel lblHeader = new JLabel("Place Reservation Waitlist");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        panel.add(lblHeader, gbc);

        RoundedTextField txtStudentId = new RoundedTextField();
        txtStudentId.setPlaceholder("Student ID");
        gbc.gridy = 1;
        panel.add(txtStudentId, gbc);

        RoundedTextField txtBookId = new RoundedTextField();
        txtBookId.setPlaceholder("Book ID");
        gbc.gridy = 2;
        panel.add(txtBookId, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(220, 220, 220), new Color(200, 200, 200));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dlg.dispose());

        RoundedButton btnSave = new RoundedButton("Reserve", new Color(24, 144, 255), new Color(11, 101, 192));
        btnSave.setPreferredSize(new Dimension(90, 35));
        btnSave.addActionListener(e -> {
            String studentIdStr = txtStudentId.getText().trim();
            String bookIdStr = txtBookId.getText().trim();

            if (studentIdStr.isEmpty() || bookIdStr.isEmpty()) {
                ToastNotification.show(parent, "Both Student and Book IDs are required", ToastNotification.Type.ERROR);
                return;
            }

            try {
                int studentId = Integer.parseInt(studentIdStr);
                int bookId = Integer.parseInt(bookIdStr);

                new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        Student s = studentService.getStudentById(studentId);
                        if (s == null) return "Student ID not found";
                        Book b = bookService.getBookById(bookId);
                        if (b == null) return "Book ID not found";
                        
                        if (b.getQuantity() > 0) {
                            return "Book copy is available in stock. Issue directly!";
                        }

                        Reservation res = new Reservation();
                        res.setStudentId(studentId);
                        res.setBookId(bookId);
                        res.setReservationDate(new Date(System.currentTimeMillis()));
                        res.setStatus("Waiting");

                        reservationService.reserveBook(res);
                        return "SUCCESS";
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            if (result.equals("SUCCESS")) {
                                ToastNotification.show(parent, "Reservation waitlisted successfully!", ToastNotification.Type.SUCCESS);
                                dlg.dispose();
                                refreshTable();
                            } else {
                                ToastNotification.show(parent, result, ToastNotification.Type.ERROR);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            } catch (NumberFormatException ex) {
                ToastNotification.show(parent, "IDs must be numeric integers", ToastNotification.Type.ERROR);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        gbc.gridy = 3;
        panel.add(btnPanel, gbc);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setSize(340, dlg.getHeight());
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void cancelSelectedReservation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "Select a reservation to cancel", ToastNotification.Type.ERROR);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmationDialog.show(parent, "Cancel Reservation", "Cancel reservation ID " + id + "?");
        if (confirmed) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    reservationService.cancelReservation(id);
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Reservation cancelled successfully", ToastNotification.Type.SUCCESS);
                    refreshTable();
                }
            }.execute();
        }
    }

    private void exportCSV() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Reservations Waitlist");
        fileChooser.setSelectedFile(new File("Reservations_Report.csv"));
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
}

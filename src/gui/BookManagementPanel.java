package gui;

import components.*;
import model.Book;
import service.BookManagement;
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

public class BookManagementPanel extends JPanel {

    private BookManagement bookService = new BookManagement();
    private ModernTable table;
    private DefaultTableModel tableModel;
    private RoundedTextField txtSearch;
    private JLabel lblDetailsTitle;
    private JLabel lblDetailsAuthor;
    private JLabel lblDetailsCategory;
    private JLabel lblDetailsQty;
    private JLabel lblDetailsPrice;
    private JLabel lblDetailsStatus;
    private Book selectedBook;

    public BookManagementPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Top Panel: Title & Actions
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Book Repository");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Actions: Export, Print, Add New Book
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Add Book", new Color(46, 204, 113), new Color(39, 174, 96));
        btnAdd.addActionListener(e -> showBookDialog(null));

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

        // Search Bar Panel
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        txtSearch = new RoundedTextField();
        txtSearch.setPlaceholder("Search books by title, author, category...");
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
        String[] columns = {"Book ID", "Title", "Author", "Category", "Qty", "Price"};
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
                    loadBookDetails(id);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Side: Details View & Operations
        RoundedPanel rightPanel = new RoundedPanel(16);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        rightPanel.setLayout(new BorderLayout());

        JPanel detailsGrid = new JPanel(new GridBagLayout());
        detailsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        lblDetailsTitle = new JLabel("Select a book to view details");
        lblDetailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailsTitle.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        detailsGrid.add(lblDetailsTitle, gbc);

        lblDetailsAuthor = new JLabel("Author: -");
        lblDetailsAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetailsAuthor.setForeground(new Color(100, 100, 100));
        gbc.gridy = 1;
        detailsGrid.add(lblDetailsAuthor, gbc);

        lblDetailsCategory = new JLabel("Category: -");
        lblDetailsCategory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetailsCategory.setForeground(new Color(100, 100, 100));
        gbc.gridy = 2;
        detailsGrid.add(lblDetailsCategory, gbc);

        lblDetailsQty = new JLabel("Available Stock: -");
        lblDetailsQty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetailsQty.setForeground(new Color(100, 100, 100));
        gbc.gridy = 3;
        detailsGrid.add(lblDetailsQty, gbc);

        lblDetailsPrice = new JLabel("Price: -");
        lblDetailsPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetailsPrice.setForeground(new Color(100, 100, 100));
        gbc.gridy = 4;
        detailsGrid.add(lblDetailsPrice, gbc);

        lblDetailsStatus = new JLabel("Status: -");
        lblDetailsStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailsStatus.setForeground(Color.GRAY);
        gbc.gridy = 5;
        detailsGrid.add(lblDetailsStatus, gbc);

        rightPanel.add(detailsGrid, BorderLayout.NORTH);

        // Edit / Delete Buttons on Right Side
        JPanel rightButtons = new JPanel(new GridLayout(2, 1, 10, 10));
        rightButtons.setOpaque(false);
        RoundedButton btnEdit = new RoundedButton("Edit details", new Color(241, 196, 15), new Color(243, 156, 18));
        btnEdit.addActionListener(e -> {
            if (selectedBook != null) showBookDialog(selectedBook);
            else ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "No book selected", ToastNotification.Type.ERROR);
        });

        RoundedButton btnDelete = new RoundedButton("Delete Book", new Color(231, 76, 60), new Color(192, 41, 43));
        btnDelete.addActionListener(e -> deleteSelectedBook());

        rightButtons.add(btnEdit);
        rightButtons.add(btnDelete);
        rightPanel.add(rightButtons, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                return bookService.getAllBooks();
            }

            @Override
            protected void done() {
                try {
                    List<Book> books = get();
                    tableModel.setRowCount(0);
                    for (Book book : books) {
                        tableModel.addRow(new Object[]{
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getCategory(),
                                book.getQuantity(),
                                book.getPrice()
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
        new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                if (search.isEmpty()) {
                    return bookService.getAllBooks();
                } else {
                    return bookService.searchBooks(search);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Book> books = get();
                    tableModel.setRowCount(0);
                    for (Book book : books) {
                        tableModel.addRow(new Object[]{
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getCategory(),
                                book.getQuantity(),
                                book.getPrice()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadBookDetails(int id) {
        new SwingWorker<Book, Void>() {
            @Override
            protected Book doInBackground() throws Exception {
                return bookService.getBookById(id);
            }

            @Override
            protected void done() {
                try {
                    selectedBook = get();
                    if (selectedBook != null) {
                        lblDetailsTitle.setText(selectedBook.getTitle());
                        lblDetailsAuthor.setText("Author: " + selectedBook.getAuthor());
                        lblDetailsCategory.setText("Category: " + selectedBook.getCategory());
                        lblDetailsQty.setText("Available Stock: " + selectedBook.getQuantity() + " units");
                        lblDetailsPrice.setText("Price: ₹" + selectedBook.getPrice());
                        if (selectedBook.getQuantity() > 0) {
                            lblDetailsStatus.setText("Status: IN STOCK");
                            lblDetailsStatus.setForeground(new Color(39, 174, 96));
                        } else {
                            lblDetailsStatus.setText("Status: OUT OF STOCK");
                            lblDetailsStatus.setForeground(new Color(235, 87, 87));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void clearDetails() {
        selectedBook = null;
        lblDetailsTitle.setText("Select a book to view details");
        lblDetailsAuthor.setText("Author: -");
        lblDetailsCategory.setText("Category: -");
        lblDetailsQty.setText("Available Stock: -");
        lblDetailsPrice.setText("Price: -");
        lblDetailsStatus.setText("Status: -");
        lblDetailsStatus.setForeground(Color.GRAY);
    }

    private void showBookDialog(Book book) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, book == null ? "Add New Book" : "Edit Book Details", true);
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

        JLabel lblHeader = new JLabel(book == null ? "Register New Book" : "Edit Book Record");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        panel.add(lblHeader, gbc);

        RoundedTextField txtTitle = new RoundedTextField();
        txtTitle.setPlaceholder("Book Title");
        gbc.gridy = 1;
        panel.add(txtTitle, gbc);

        RoundedTextField txtAuthor = new RoundedTextField();
        txtAuthor.setPlaceholder("Author Name");
        gbc.gridy = 2;
        panel.add(txtAuthor, gbc);

        RoundedTextField txtCategory = new RoundedTextField();
        txtCategory.setPlaceholder("Category (e.g. Science, Literature)");
        gbc.gridy = 3;
        panel.add(txtCategory, gbc);

        RoundedTextField txtQty = new RoundedTextField();
        txtQty.setPlaceholder("Quantity Stock");
        gbc.gridy = 4;
        panel.add(txtQty, gbc);

        RoundedTextField txtPrice = new RoundedTextField();
        txtPrice.setPlaceholder("Price (INR)");
        gbc.gridy = 5;
        panel.add(txtPrice, gbc);

        if (book != null) {
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtQty.setText(String.valueOf(book.getQuantity()));
            txtPrice.setText(String.valueOf(book.getPrice()));
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
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String category = txtCategory.getText().trim();
            String qtyStr = txtQty.getText().trim();
            String priceStr = txtPrice.getText().trim();

            if (title.isEmpty() || author.isEmpty() || category.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
                ToastNotification.show(parent, "All fields are required", ToastNotification.Type.ERROR);
                return;
            }

            try {
                int qty = Integer.parseInt(qtyStr);
                double price = Double.parseDouble(priceStr);

                Book b = book == null ? new Book() : book;
                b.setTitle(title);
                b.setAuthor(author);
                b.setCategory(category);
                b.setQuantity(qty);
                b.setPrice(price);

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        if (book == null) {
                            bookService.addBook(b);
                        } else {
                            bookService.updateBook(b);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        ToastNotification.show(parent, book == null ? "Book registered successfully" : "Book updated successfully", ToastNotification.Type.SUCCESS);
                        dlg.dispose();
                        refreshTable();
                    }
                }.execute();
            } catch (NumberFormatException ex) {
                ToastNotification.show(parent, "Invalid quantity or price format", ToastNotification.Type.ERROR);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        gbc.gridy = 6;
        panel.add(btnPanel, gbc);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setSize(380, dlg.getHeight());
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void deleteSelectedBook() {
        if (selectedBook == null) {
            ToastNotification.show((Frame) SwingUtilities.getWindowAncestor(this), "No book selected", ToastNotification.Type.ERROR);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmationDialog.show(parent, "Delete Book", "Are you sure you want to delete '" + selectedBook.getTitle() + "'?");
        if (confirmed) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bookService.deleteBook(selectedBook.getBookId());
                    return null;
                }

                @Override
                protected void done() {
                    ToastNotification.show(parent, "Book deleted successfully", ToastNotification.Type.SUCCESS);
                    refreshTable();
                }
            }.execute();
        }
    }

    private void exportCSV() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Books List");
        fileChooser.setSelectedFile(new File("Books_Report.csv"));
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
        PDFExporter.printTable(table, "Library Books Inventory Report", "LMS ERP System");
    }
}

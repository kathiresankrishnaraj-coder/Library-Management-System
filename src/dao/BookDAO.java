package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Book;

public class BookDAO {

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Add Book
    public void addBook(Book book) {
        String query = "INSERT INTO books(title,author,category,quantity,price) VALUES(?,?,?,?,?)";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            ps.setDouble(5, book.getPrice());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book Added Successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // View All Books (Console Output)
    public void viewBooks() {
        String query = "SELECT * FROM books";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("----------------------------------");
                System.out.println("Book ID   : " + rs.getInt("book_id"));
                System.out.println("Title     : " + rs.getString("title"));
                System.out.println("Author    : " + rs.getString("author"));
                System.out.println("Category  : " + rs.getString("category"));
                System.out.println("Quantity  : " + rs.getInt("quantity"));
                System.out.println("Price     : " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Fetch All Books (List Return for GUI)
    public List<Book> getAllBooksList() {
        List<Book> list = new ArrayList<>();
        String query = "SELECT * FROM books";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setQuantity(rs.getInt("quantity"));
                book.setPrice(rs.getDouble("price"));
                list.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Live Search Books (List Return for GUI)
    public List<Book> searchBooksList(String search) {
        List<Book> list = new ArrayList<>();
        String query = "SELECT * FROM books WHERE book_id LIKE ? OR title LIKE ? OR author LIKE ? OR category LIKE ?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            String wildcard = "%" + search + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            ps.setString(4, wildcard);
            rs = ps.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setQuantity(rs.getInt("quantity"));
                book.setPrice(rs.getDouble("price"));
                list.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Search Book (Single Return)
    public Book searchBook(int id) {
        String query = "SELECT * FROM books WHERE book_id=?";
        Book book = null;
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setQuantity(rs.getInt("quantity"));
                book.setPrice(rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return book;
    }

    // Update Book
    public void updateBook(Book book) {
        String query = "UPDATE books SET title=?, author=?, category=?, quantity=?, price=? WHERE book_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            ps.setDouble(5, book.getPrice());
            ps.setInt(6, book.getBookId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book Updated Successfully.");
            } else {
                System.out.println("Book Not Found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    public void showAvailableBooks() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM books WHERE quantity > 0";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n==============================================");
            System.out.println("          AVAILABLE BOOKS");
            System.out.println("==============================================");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Book ID    : " + rs.getInt("book_id"));
                System.out.println("Title      : " + rs.getString("title"));
                System.out.println("Author     : " + rs.getString("author"));
                System.out.println("Category   : " + rs.getString("category"));
                System.out.println("Quantity   : " + rs.getInt("quantity"));
                System.out.println("Price      : ₹" + rs.getDouble("price"));
                System.out.println("----------------------------------------------");
            }
            if (!found) {
                System.out.println("No books are currently available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    public void showBookStatus() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM books";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n======================================================================================");
            System.out.printf("%-8s %-25s %-20s %-15s %-10s %-10s %-15s\n",
                    "ID", "TITLE", "AUTHOR", "CATEGORY", "QTY", "PRICE", "STATUS");
            System.out.println("======================================================================================");

            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                String status = (quantity > 0) ? "Available" : "Out of Stock";

                System.out.printf("%-8d %-25s %-20s %-15s %-10d ₹%-9.2f %-15s\n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        quantity,
                        rs.getDouble("price"),
                        status);
            }
            System.out.println("======================================================================================");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Delete Book
    public void deleteBook(int id) {
        String query = "DELETE FROM books WHERE book_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book Deleted Successfully.");
            } else {
                System.out.println("Book Not Found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
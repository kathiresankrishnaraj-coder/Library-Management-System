package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.IssueBook;

public class IssueBookDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Issue Book
    public void issueBook(IssueBook issue) {
        try {
            con = DBConnection.getConnection();

            // Check available quantity
            String checkSql = "SELECT quantity FROM books WHERE book_id=?";
            ps = con.prepareStatement(checkSql);
            ps.setInt(1, issue.getBookId());
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Book ID not found.");
                return;
            }

            int quantity = rs.getInt("quantity");
            if (quantity <= 0) {
                System.out.println("Book is not available.");
                return;
            }
            rs.close();
            ps.close();
            
            // Check Student Borrow Limit
            String limitSql = "SELECT COUNT(*) FROM issue_books WHERE student_id=? AND status='Issued'";
            ps = con.prepareStatement(limitSql);
            ps.setInt(1, issue.getStudentId());
            rs = ps.executeQuery();

            if (rs.next()) {
                int totalBooks = rs.getInt(1);
                if (totalBooks >= 3) {
                    System.out.println("--------------------------------");
                    System.out.println("Borrow Limit Reached");
                    System.out.println("A student can borrow only 3 books.");
                    System.out.println("--------------------------------");
                    return;
                }
            }
            rs.close();
            ps.close();

            // Issue the book
            String issueSql = "INSERT INTO issue_books(book_id, student_id, issue_date, return_date, status, fine) VALUES(?,?,?,?,?,?)";
            ps = con.prepareStatement(issueSql);
            ps.setInt(1, issue.getBookId());
            ps.setInt(2, issue.getStudentId());
            ps.setDate(3, issue.getIssueDate());
            ps.setDate(4, issue.getReturnDate());
            ps.setString(5, "Issued");
            ps.setDouble(6, 0.0);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Reduce quantity
                String updateSql = "UPDATE books SET quantity = quantity - 1 WHERE book_id=?";
                ps = con.prepareStatement(updateSql);
                ps.setInt(1, issue.getBookId());
                ps.executeUpdate();
                System.out.println("Book Issued Successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    public void returnBook(int bookId, int studentId) {
        try {
            con = DBConnection.getConnection();

            // Get return date
            String sql = "SELECT return_date FROM issue_books WHERE book_id=? AND student_id=? AND status='Issued'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("No issued book found.");
                return;
            }

            java.sql.Date dueDate = rs.getDate("return_date");
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            long diff = today.getTime() - dueDate.getTime();
            long lateDays = diff / (1000 * 60 * 60 * 24);
            if (lateDays < 0) {
                lateDays = 0;
            }

            double fine = lateDays * 5;
            rs.close();
            ps.close();

            // Update issue record
            String updateIssue = "UPDATE issue_books SET status='Returned', fine=? WHERE book_id=? AND student_id=? AND status='Issued'";
            ps = con.prepareStatement(updateIssue);
            ps.setDouble(1, fine);
            ps.setInt(2, bookId);
            ps.setInt(3, studentId);
            ps.executeUpdate();
            ps.close();

            // Increase quantity
            String updateBook = "UPDATE books SET quantity=quantity+1 WHERE book_id=?";
            ps = con.prepareStatement(updateBook);
            ps.setInt(1, bookId);
            ps.executeUpdate();

            System.out.println("Book Returned Successfully.");
            System.out.println("Late Days : " + lateDays);
            System.out.println("Fine : ₹" + fine);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // List Return for JTable
    public List<IssueBook> getAllIssuedBooksList() {
        List<IssueBook> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_books";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                IssueBook issue = new IssueBook();
                issue.setIssueId(rs.getInt("issue_id"));
                issue.setBookId(rs.getInt("book_id"));
                issue.setStudentId(rs.getInt("student_id"));
                issue.setIssueDate(rs.getDate("issue_date"));
                issue.setReturnDate(rs.getDate("return_date"));
                issue.setStatus(rs.getString("status"));
                issue.setFine(rs.getDouble("fine"));
                list.add(issue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // List Return for Overdue JTable
    public List<IssueBook> getOverdueBooksList() {
        List<IssueBook> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_books WHERE return_date < CURDATE() AND status='Issued'";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                IssueBook issue = new IssueBook();
                issue.setIssueId(rs.getInt("issue_id"));
                issue.setBookId(rs.getInt("book_id"));
                issue.setStudentId(rs.getInt("student_id"));
                issue.setIssueDate(rs.getDate("issue_date"));
                issue.setReturnDate(rs.getDate("return_date"));
                issue.setStatus(rs.getString("status"));
                issue.setFine(rs.getDouble("fine"));
                list.add(issue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Fetch single active issue
    public IssueBook getActiveIssue(int bookId, int studentId) {
        IssueBook issue = null;
        String sql = "SELECT * FROM issue_books WHERE book_id=? AND student_id=? AND status='Issued'";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                issue = new IssueBook();
                issue.setIssueId(rs.getInt("issue_id"));
                issue.setBookId(rs.getInt("book_id"));
                issue.setStudentId(rs.getInt("student_id"));
                issue.setIssueDate(rs.getDate("issue_date"));
                issue.setReturnDate(rs.getDate("return_date"));
                issue.setStatus(rs.getString("status"));
                issue.setFine(rs.getDouble("fine"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return issue;
    }

    // Get borrow count for specific student
    public int getBorrowCountForStudent(int studentId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM issue_books WHERE student_id=? AND status='Issued'";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return count;
    }

    // Get outstanding fines for specific student
    public double getOutstandingFinesForStudent(int studentId) {
        double fine = 0.0;
        String sql = "SELECT SUM(fine) FROM issue_books WHERE student_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                fine = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return fine;
    }

    public void viewIssuedBooks() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM issue_books";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("--------------------------------------");
                System.out.println("Issue ID    : " + rs.getInt("issue_id"));
                System.out.println("Book ID     : " + rs.getInt("book_id"));
                System.out.println("Student ID  : " + rs.getInt("student_id"));
                System.out.println("Issue Date  : " + rs.getDate("issue_date"));
                System.out.println("Return Date : " + rs.getDate("return_date"));
                System.out.println("Status      : " + rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    public void showOverdueBooks() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM issue_books WHERE return_date < CURDATE() AND status='Issued'";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n==============================================");
            System.out.println("           OVERDUE BOOKS");
            System.out.println("==============================================");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Issue ID     : " + rs.getInt("issue_id"));
                System.out.println("Book ID      : " + rs.getInt("book_id"));
                System.out.println("Student ID   : " + rs.getInt("student_id"));
                System.out.println("Issue Date   : " + rs.getDate("issue_date"));
                System.out.println("Due Date     : " + rs.getDate("return_date"));
                System.out.println("Status       : " + rs.getString("status"));
                System.out.println("----------------------------------------------");
            }

            if (!found) {
                System.out.println("No overdue books found.");
            }
        } catch (Exception e) {
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
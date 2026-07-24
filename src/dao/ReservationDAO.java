package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Reservation;

public class ReservationDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Reserve Book
    public void reserveBook(Reservation reservation) {
        try {
            con = DBConnection.getConnection();

            // Check Quantity
            String checkBook = "SELECT quantity,title FROM books WHERE book_id=?";
            ps = con.prepareStatement(checkBook);
            ps.setInt(1, reservation.getBookId());
            rs = ps.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                if (quantity > 0) {
                    System.out.println("\nBook is Available.");
                    System.out.println("Please Issue the Book.");
                    return;
                }
            } else {
                System.out.println("Book Not Found.");
                return;
            }
            rs.close();
            ps.close();

            String sql = "INSERT INTO reservations(student_id,book_id,reservation_date,status) VALUES(?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, reservation.getStudentId());
            ps.setInt(2, reservation.getBookId());
            ps.setDate(3, reservation.getReservationDate());
            ps.setString(4, "Waiting");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\nReservation Successful.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Fetch All Reservations (List Return for GUI)
    public List<Reservation> getAllReservationsList() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.student_id, r.book_id, r.reservation_date, r.status, s.name as student_name, b.title as book_title " +
                     "FROM reservations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN books b ON r.book_id = b.book_id";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setReservationId(rs.getInt("reservation_id"));
                res.setStudentId(rs.getInt("student_id"));
                res.setBookId(rs.getInt("book_id"));
                res.setReservationDate(rs.getDate("reservation_date"));
                res.setStatus(rs.getString("status"));
                res.setStudentName(rs.getString("student_name"));
                res.setBookTitle(rs.getString("book_title"));
                list.add(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    public void viewReservations() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT r.reservation_id, s.name, b.title, r.reservation_date, r.status " +
                         "FROM reservations r " +
                         "JOIN students s ON r.student_id = s.student_id " +
                         "JOIN books b ON r.book_id = b.book_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n==============================================================");
            System.out.printf("%-5s %-20s %-25s %-15s %-10s%n",
                    "ID", "Student", "Book", "Date", "Status");
            System.out.println("==============================================================");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s %-15s %-10s%n",
                        rs.getInt("reservation_id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getDate("reservation_date"),
                        rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Cancel Reservation
    public void cancelReservation(int id) {
        try {
            con = DBConnection.getConnection();
            String sql = "DELETE FROM reservations WHERE reservation_id=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Reservation Cancelled.");
            } else {
                System.out.println("Reservation Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Check Reservation After Return
    public void checkReservation(int bookId) {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM reservations WHERE book_id=? AND status='Waiting' ORDER BY reservation_date LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n===============================");
                System.out.println("Reserved Book Available");
                System.out.println("Student ID : " + rs.getInt("student_id"));
                System.out.println("===============================");
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
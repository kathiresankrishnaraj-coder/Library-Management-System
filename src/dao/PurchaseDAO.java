package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Purchase;

public class PurchaseDAO {

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public void purchaseBook(Purchase purchase) {
        try {
            con = DBConnection.getConnection();

            // Start Transaction
            con.setAutoCommit(false);

            // 1. Check whether Book exists
            String checkBook = "SELECT quantity FROM books WHERE book_id=?";
            ps = con.prepareStatement(checkBook);
            ps.setInt(1, purchase.getBookId());
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Book ID Not Found.");
                con.rollback();
                return;
            }

            rs.close();
            ps.close();

            // 2. Insert Purchase History
            String insertPurchase = "INSERT INTO purchase_history(book_id,supplier_name,quantity,price_per_book,total_amount,purchase_date) VALUES(?,?,?,?,?,?)";
            ps = con.prepareStatement(insertPurchase);
            ps.setInt(1, purchase.getBookId());
            ps.setString(2, purchase.getSupplierName());
            ps.setInt(3, purchase.getQuantity());
            ps.setDouble(4, purchase.getPricePerBook());
            ps.setDouble(5, purchase.getTotalAmount());
            ps.setDate(6, purchase.getPurchaseDate());

            int purchaseRows = ps.executeUpdate();
            ps.close();

            // 3. Update Book Quantity
            String updateBook = "UPDATE books SET quantity = quantity + ? WHERE book_id=?";
            ps = con.prepareStatement(updateBook);
            ps.setInt(1, purchase.getQuantity());
            ps.setInt(2, purchase.getBookId());

            int updateRows = ps.executeUpdate();

            // 4. Commit Transaction
            if (purchaseRows > 0 && updateRows > 0) {
                con.commit();
                System.out.println("\n=================================");
                System.out.println("Book Purchased Successfully");
                System.out.println("Stock Updated Successfully");
                System.out.println("Transaction Committed");
                System.out.println("=================================");
            } else {
                con.rollback();
                System.out.println("Transaction Failed");
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                    System.out.println("Transaction Rolled Back");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Fetch All Purchases (List Return for GUI)
    public List<Purchase> getAllPurchasesList() {
        List<Purchase> list = new ArrayList<>();
        String sql = "SELECT p.purchase_id, p.book_id, p.supplier_name, p.quantity, p.price_per_book, p.total_amount, p.purchase_date, b.title as book_title " +
                     "FROM purchase_history p " +
                     "JOIN books b ON p.book_id = b.book_id";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Purchase p = new Purchase();
                p.setPurchaseId(rs.getInt("purchase_id"));
                p.setBookId(rs.getInt("book_id"));
                p.setSupplierName(rs.getString("supplier_name"));
                p.setQuantity(rs.getInt("quantity"));
                p.setPricePerBook(rs.getDouble("price_per_book"));
                p.setTotalAmount(rs.getDouble("total_amount"));
                p.setPurchaseDate(rs.getDate("purchase_date"));
                p.setBookTitle(rs.getString("book_title"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void viewPurchaseHistory() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT p.purchase_id, b.title, p.supplier_name, " +
                         "p.quantity, p.price_per_book, p.total_amount, p.purchase_date " +
                         "FROM purchase_history p " +
                         "JOIN books b ON p.book_id = b.book_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("==============================================================");
            while (rs.next()) {
                System.out.println("Purchase ID : " + rs.getInt("purchase_id"));
                System.out.println("Book Title  : " + rs.getString("title"));
                System.out.println("Supplier    : " + rs.getString("supplier_name"));
                System.out.println("Quantity    : " + rs.getInt("quantity"));
                System.out.println("Price       : " + rs.getDouble("price_per_book"));
                System.out.println("Total       : " + rs.getDouble("total_amount"));
                System.out.println("Date        : " + rs.getDate("purchase_date"));
                System.out.println("-----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
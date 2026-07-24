package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Notification;

public class NotificationDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Add Notification
    public void addNotification(String message) {
        try {
            con = DBConnection.getConnection();
            String sql = "INSERT INTO notifications(message, status) VALUES(?, 'Unread')";
            ps = con.prepareStatement(sql);
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Fetch All Notifications (List Return for GUI)
    public List<Notification> getAllNotificationsList() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY notification_date DESC";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setNotificationId(rs.getInt("notification_id"));
                n.setMessage(rs.getString("message"));
                n.setNotificationDate(rs.getTimestamp("notification_date"));
                n.setStatus(rs.getString("status"));
                list.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Get Unread Count for Badge
    public int getUnreadCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM notifications WHERE status='Unread'";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
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

    // Delete Notification
    public void deleteNotification(int id) {
        String sql = "DELETE FROM notifications WHERE notification_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // View Notifications
    public void viewNotifications() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM notifications ORDER BY notification_date DESC";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n================ NOTIFICATIONS ================");
            while (rs.next()) {
                System.out.println("ID      : " + rs.getInt("notification_id"));
                System.out.println("Message : " + rs.getString("message"));
                System.out.println("Date    : " + rs.getTimestamp("notification_date"));
                System.out.println("Status  : " + rs.getString("status"));
                System.out.println("---------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Mark all notifications as read
    public void markAllAsRead() {
        try {
            con = DBConnection.getConnection();
            String sql = "UPDATE notifications SET status='Read' WHERE status='Unread'";
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
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
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.User;

public class UserDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Login
    public User login(String username, String password) {
        User user = null;
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return user;
    }

    // Add User
    public void addUser(User user) {
        try {
            con = DBConnection.getConnection();
            String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("User Added Successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Fetch All Users (List Return for GUI)
    public List<User> getAllUsersList() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // View Users
    public void viewUsers() {
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM users";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n==============================================================");
            System.out.printf("%-10s %-20s %-20s\n", "ID", "USERNAME", "ROLE");
            System.out.println("==============================================================");

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s\n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Delete User
    public void deleteUser(int id) {
        try {
            con = DBConnection.getConnection();
            String sql = "DELETE FROM users WHERE user_id=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("User Deleted Successfully.");
            } else {
                System.out.println("User Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Update Role
    public void updateRole(int id, String role) {
        try {
            con = DBConnection.getConnection();
            String sql = "UPDATE users SET role=? WHERE user_id=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, role);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Role Updated Successfully.");
            } else {
                System.out.println("User Not Found.");
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
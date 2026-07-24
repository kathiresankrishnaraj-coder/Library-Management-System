package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;

public class AdminDAO {

    public boolean login(String username, String password) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            String sql = "SELECT * FROM admin WHERE username=? AND password=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(con, ps, rs);
        }
        return false;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();

            // Verify old password
            String checkSql = "SELECT * FROM admin WHERE username=? AND password=?";
            ps = con.prepareStatement(checkSql);
            ps.setString(1, username);
            ps.setString(2, oldPassword);
            rs = ps.executeQuery();

            if (rs.next()) {
                rs.close();
                ps.close();

                String updateSql = "UPDATE admin SET password=? WHERE username=?";
                ps = con.prepareStatement(updateSql);
                ps.setString(1, newPassword);
                ps.setString(2, username);
                int rows = ps.executeUpdate();
                return rows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(con, ps, rs);
        }
        return false;
    }

    private void closeResources(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
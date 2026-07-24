package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Student;

public class StudentDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Add Student
    public void addStudent(Student student) {
        String sql = "INSERT INTO students(name,department,phone) VALUES(?,?,?)";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, student.getName());
            ps.setString(2, student.getDepartment());
            ps.setString(3, student.getPhone());

            if (ps.executeUpdate() > 0) {
                System.out.println("Student Added Successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // View Students
    public void viewStudents() {
        String sql = "SELECT * FROM students";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("-----------------------------");
                System.out.println("Student ID : " + rs.getInt("student_id"));
                System.out.println("Name       : " + rs.getString("name"));
                System.out.println("Department : " + rs.getString("department"));
                System.out.println("Phone      : " + rs.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Fetch All Students (List Return for GUI)
    public List<Student> getAllStudentsList() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setPhone(rs.getString("phone"));
                list.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Search Students List (List Return for GUI)
    public List<Student> searchStudentsList(String search) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR name LIKE ? OR department LIKE ? OR phone LIKE ?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            String wildcard = "%" + search + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            ps.setString(4, wildcard);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setPhone(rs.getString("phone"));
                list.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Search Student (Single Return)
    public Student searchStudent(int id) {
        Student student = null;
        String sql = "SELECT * FROM students WHERE student_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setPhone(rs.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return student;
    }

    // Update Student
    public void updateStudent(Student student) {
        String sql = "UPDATE students SET name=?, department=?, phone=? WHERE student_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, student.getName());
            ps.setString(2, student.getDepartment());
            ps.setString(3, student.getPhone());
            ps.setInt(4, student.getStudentId());

            if (ps.executeUpdate() > 0) {
                System.out.println("Student Updated Successfully.");
            } else {
                System.out.println("Student Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Delete Student
    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE student_id=?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                System.out.println("Student Deleted Successfully.");
            } else {
                System.out.println("Student Not Found.");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


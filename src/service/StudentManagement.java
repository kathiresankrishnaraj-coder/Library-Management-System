package service;

import java.util.List;
import java.util.Scanner;

import dao.StudentDAO;
import model.Student;

public class StudentManagement {

    private Scanner sc = new Scanner(System.in);
    private StudentDAO studentDAO = new StudentDAO();

    // GUI delegator methods
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudentsList();
    }

    public List<Student> searchStudents(String search) {
        return studentDAO.searchStudentsList(search);
    }

    public Student getStudentById(int id) {
        return studentDAO.searchStudent(id);
    }

    public void addStudent(Student student) {
        studentDAO.addStudent(student);
    }

    public void updateStudent(Student student) {
        studentDAO.updateStudent(student);
    }

    public void deleteStudent(int id) {
        studentDAO.deleteStudent(id);
    }

    // Console methods
    public void addStudent() {
        Student student = new Student();
        System.out.print("Enter Student Name: ");
        student.setName(sc.nextLine());

        System.out.print("Enter Department: ");
        student.setDepartment(sc.nextLine());

        System.out.print("Enter Phone Number: ");
        student.setPhone(sc.nextLine());

        studentDAO.addStudent(student);
    }

    public void viewStudents() {
        studentDAO.viewStudents();
    }

    public void searchStudent() {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        Student student = studentDAO.searchStudent(id);
        if (student != null) {
            System.out.println(student);
        } else {
            System.out.println("Student Not Found.");
        }
    }

    public void updateStudent() {
        Student student = new Student();
        System.out.print("Enter Student ID: ");
        student.setStudentId(sc.nextInt());
        sc.nextLine();

        System.out.print("Enter Name: ");
        student.setName(sc.nextLine());

        System.out.print("Enter Department: ");
        student.setDepartment(sc.nextLine());

        System.out.print("Enter Phone: ");
        student.setPhone(sc.nextLine());

        studentDAO.updateStudent(student);
    }

    public void deleteStudent() {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        studentDAO.deleteStudent(id);
    }
}
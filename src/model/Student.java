package model;

public class Student {

    private int studentId;
    private String name;
    private String department;
    private String phone;

    // Default Constructor
    public Student() {

    }

    // Parameterized Constructor
    public Student(int studentId, String name, String department, String phone) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.phone = phone;
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Student ID : " + studentId +
               "\nName : " + name +
               "\nDepartment : " + department +
               "\nPhone : " + phone;
    }
}

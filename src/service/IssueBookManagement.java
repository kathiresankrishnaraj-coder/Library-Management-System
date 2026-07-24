package service;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import dao.IssueBookDAO;
import model.IssueBook;

public class IssueBookManagement {

    Scanner sc = new Scanner(System.in);
    IssueBookDAO issueDAO = new IssueBookDAO();

    // GUI delegators
    public List<IssueBook> getAllIssuedBooks() {
        return issueDAO.getAllIssuedBooksList();
    }

    public List<IssueBook> getOverdueBooks() {
        return issueDAO.getOverdueBooksList();
    }

    public IssueBook getActiveIssue(int bookId, int studentId) {
        return issueDAO.getActiveIssue(bookId, studentId);
    }

    public void issueBook(IssueBook issue) {
        issueDAO.issueBook(issue);
    }

    public void returnBook(int bookId, int studentId) {
        issueDAO.returnBook(bookId, studentId);
    }

    public int getBorrowCountForStudent(int studentId) {
        return issueDAO.getBorrowCountForStudent(studentId);
    }

    public double getOutstandingFinesForStudent(int studentId) {
        return issueDAO.getOutstandingFinesForStudent(studentId);
    }

    // Issue Book (Console version)
    public void issueBook() {
        IssueBook issue = new IssueBook();
        System.out.print("Enter Book ID: ");
        issue.setBookId(sc.nextInt());

        System.out.print("Enter Student ID: ");
        issue.setStudentId(sc.nextInt());
        sc.nextLine();

        System.out.print("Enter Issue Date (YYYY-MM-DD): ");
        String issueDate = sc.nextLine();

        System.out.print("Enter Return Date (YYYY-MM-DD): ");
        String returnDate = sc.nextLine();

        try {
            issue.setIssueDate(Date.valueOf(issueDate));
            issue.setReturnDate(Date.valueOf(returnDate));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD.");
            return;
        }

        issue.setStatus("Issued");
        issueDAO.issueBook(issue);
    }

    // Return Book (Console version)
    public void returnBook() {
        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        System.out.print("Enter Student ID: ");
        int studentId = sc.nextInt();

        issueDAO.returnBook(bookId, studentId);
    }

    // View Issued Books
    public void viewIssuedBooks() {
        issueDAO.viewIssuedBooks();
    }

    public void showOverdueBooks() {
        issueDAO.showOverdueBooks();
    }
}
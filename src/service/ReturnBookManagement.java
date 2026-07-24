package service;

import java.util.Scanner;

import dao.IssueBookDAO;

public class ReturnBookManagement {

    private Scanner sc = new Scanner(System.in);
    private IssueBookDAO issueBookDAO = new IssueBookDAO();

    public void returnBook() {

        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        System.out.print("Enter Student ID: ");
        int studentId = sc.nextInt();

        issueBookDAO.returnBook(bookId, studentId);
    }
}
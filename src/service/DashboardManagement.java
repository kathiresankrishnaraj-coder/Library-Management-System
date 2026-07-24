package service;

import dao.DashboardDAO;

public class DashboardManagement {

    DashboardDAO dashboard = new DashboardDAO();

    // GUI delegators
    public int getTotalBooks() {
        return dashboard.getTotalBooks();
    }

    public int getAvailableBooks() {
        return dashboard.getAvailableBooks();
    }

    public int getIssuedBooks() {
        return dashboard.getIssuedBooks();
    }

    public int getTotalStudents() {
        return dashboard.getTotalStudents();
    }

    // Console method
    public void showDashboard() {
        System.out.println("\n========================================");
        System.out.println("        LIBRARY DASHBOARD");
        System.out.println("========================================");

        System.out.println("Total Books      : " + dashboard.getTotalBooks());
        System.out.println("Available Books  : " + dashboard.getAvailableBooks());
        System.out.println("Issued Books     : " + dashboard.getIssuedBooks());
        System.out.println("Total Students   : " + dashboard.getTotalStudents());

        System.out.println("========================================");
    }
}
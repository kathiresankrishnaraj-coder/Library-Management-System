package service;

import java.util.Scanner;

import dao.AdminDAO;

public class AdminManagement {

    private Scanner sc = new Scanner(System.in);
    private AdminDAO adminDAO = new AdminDAO();

    public boolean login() {

        System.out.println("==================================");
        System.out.println("     LIBRARY ADMIN LOGIN");
        System.out.println("==================================");

        System.out.print("Username : ");
        String username = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        if(adminDAO.login(username, password)) {

            System.out.println("\nLogin Successful.\n");
            return true;

        } else {

            System.out.println("\nInvalid Username or Password.");
            return false;
        }
    }
    
    public void changePassword() {

        System.out.println("\n===== CHANGE PASSWORD =====");

        System.out.print("Enter Username : ");
        String username = sc.nextLine();

        System.out.print("Enter Old Password : ");
        String oldPassword = sc.nextLine();

        System.out.print("Enter New Password : ");
        String newPassword = sc.nextLine();

        System.out.print("Confirm New Password : ");
        String confirmPassword = sc.nextLine();

        if (!newPassword.equals(confirmPassword)) {

            System.out.println("New Password and Confirm Password do not match.");
            return;
        }

        boolean success = adminDAO.changePassword(username, oldPassword, newPassword);

        if (success) {
            System.out.println("Password Changed Successfully.");
        } else {
            System.out.println("Invalid Username or Old Password.");
        }
    }
}
package service;

import java.util.List;
import java.util.Scanner;

import dao.UserDAO;
import model.User;

public class UserManagement {

    Scanner sc = new Scanner(System.in);
    UserDAO dao = new UserDAO();

    // GUI delegators
    public User loginUser(String username, String password) {
        return dao.login(username, password);
    }

    public List<User> getAllUsers() {
        return dao.getAllUsersList();
    }

    public void addUser(User user) {
        dao.addUser(user);
    }

    public void deleteUser(int id) {
        dao.deleteUser(id);
    }

    public void updateRole(int id, String role) {
        dao.updateRole(id, role);
    }

    // Console methods
    public User login(String expectedRole){
        System.out.println("\n========== "+expectedRole.toUpperCase()+" LOGIN ==========");
        System.out.print("Username : ");
        String username = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        User user = dao.login(username, password);
        if(user == null){
            System.out.println("Invalid Username or Password");
            return null;
        }

        if(!user.getRole().equalsIgnoreCase(expectedRole)){
            System.out.println("Access Denied!");
            return null;
        }

        System.out.println("\nWelcome " + user.getUsername());
        System.out.println("Role : " + user.getRole());
        return user;
    }
    
    public void addUser(){
        User user = new User();
        System.out.print("Username : ");
        user.setUsername(sc.nextLine());

        System.out.print("Password : ");
        user.setPassword(sc.nextLine());

        System.out.print("Role(Admin/Librarian): ");
        user.setRole(sc.nextLine());

        dao.addUser(user);
    }

    public void viewUsers(){
        dao.viewUsers();
    }

    public void deleteUser(){
        System.out.print("Enter User ID : ");
        int id = Integer.parseInt(sc.nextLine());
        dao.deleteUser(id);
    }

    public void updateRole(){
        System.out.print("Enter User ID : ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter New Role(Admin/Librarian): ");
        String role = sc.nextLine();

        dao.updateRole(id,role);
    }
}
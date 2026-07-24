package model;

public class User {

    private int userId;
    private String username;
    private String password;
    private String role;

    // Default Constructor
    public User() {

    }

    // Parameterized Constructor
    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter and Setter for User ID
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and Setter for Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Display User Information
    @Override
    public String toString() {
        return "User ID : " + userId +
               "\nUsername : " + username +
               "\nRole : " + role;
    }
}
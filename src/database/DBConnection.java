package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_management";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "root";

    public static Connection getConnection() {

        Connection con = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL Driver Not Found!");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        return con;
    }
}
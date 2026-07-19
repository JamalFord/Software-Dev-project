package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/employeeData?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Pepper.2005@";

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load MySQL JDBC driver to support all Java environments
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                // Let DriverManager try on its own if classes aren't explicitly registered
            }
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

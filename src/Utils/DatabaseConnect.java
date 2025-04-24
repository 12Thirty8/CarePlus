package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {

    String url = "jdbc:mysql://localhost:3306/careplus";
    String user = "root";
    String password = "";

    public Connection connect() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.err.println("MySQL JDBC driver not found.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error connecting to the database.");
        }
        return con;
    }
}
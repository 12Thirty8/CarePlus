package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/careplus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.err.println("MySQL JDBC driver not found.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error connecting to the database.");
        }
        return con;
    }

    public static String getCOHName() {
        String name = null;
        String query = "SELECT f_name, l_name FROM employee WHERE dep_id = 3 LIMIT 1";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String firstName = rs.getString("f_name");
                String lastName = rs.getString("l_name");
                name = firstName + " " + lastName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static String getPharmacistName(int employeeId) {
        String name = null;
        String query = "SELECT f_name, l_name FROM employee WHERE employee_id = ? AND dep_id = 2";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("f_name");
                String lastName = rs.getString("l_name");
                name = firstName + " " + lastName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static String getNurseName(int employeeId) {
        String name = null;
        String query = "SELECT f_name, l_name FROM employee WHERE employee_id = ? AND dep_id = 1";

        System.out.println("DEBUG - Executing query: " + query); // Add this
        System.out.println("DEBUG - Using employee ID: " + employeeId); // Add this

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Found nurse record");
                String firstName = rs.getString("f_name");
                String lastName = rs.getString("l_name");
                name = firstName + " " + lastName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DEBUG - Found name: " + name); // Add this
        return name;
    }

}
package com.example.flightreservationsystemfinal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/java_project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed! Please check your DB_USER and DB_PASSWORD in Database.java");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Object databaseQuery(String query, Object... params) {
        if (connection == null) {
            System.err.println("Cannot execute query because database connection is not available.");
            return null;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            boolean isSelect = query.trim().toLowerCase().startsWith("select");

            if (isSelect) {
                return statement.executeQuery();
            } else {
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
package com.example.flightreservationsystemfinal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class User {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String role;

    public User() {}

    public User(int id, String username, String firstName, String lastName, String phoneNo, String role) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.role = role;
    }

    // --- GETTERS for JavaFX PropertyValueFactory ---
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNo() { return phoneNo; }
    public String getRole() { return role; }

    public static boolean promoteUserToAdmin(String username) {
        int affectedRows = (int) Database.databaseQuery("UPDATE users SET role = 'admin' WHERE username = ? AND role = 'passenger';", username);
        return affectedRows > 0;
    }

    public static boolean demoteAdminToUser(String username) {
        int affectedRows = (int) Database.databaseQuery("UPDATE users SET role = 'passenger' WHERE username = ? AND role = 'admin';", username);
        return affectedRows > 0;
    }

    public static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        ResultSet rs = (ResultSet) Database.databaseQuery("SELECT id, username, first_name, last_name, phone_no, role FROM users;");
        if (rs == null) return users;

        while(rs.next()) {
            users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("phone_no"),
                    rs.getString("role")
            ));
        }
        rs.close();
        return users;
    }

    public static User userLogin(String username, String password, String expectedRole) {
        try {
            ResultSet rs = (ResultSet) Database.databaseQuery("SELECT * FROM users WHERE username = ?;", username);

            if (rs != null && rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    String actualRole = rs.getString("role");
                    if (expectedRole.equalsIgnoreCase(actualRole)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("phone_no"),
                                actualRole
                        );
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean registerUser(String username, String password, String firstName, String lastName, String phoneNo, String role) {
        try {
            if (!checkUsername(username)) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            int affectedRows = (int) Database.databaseQuery(
                    "INSERT INTO users (username, password, first_name, last_name, phone_no, role) VALUES (?, ?, ?, ?, ?, ?);",
                    username, hashedPassword, firstName, lastName, phoneNo, role
            );
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkUsername(String username) throws SQLException {
        ResultSet rs = (ResultSet) Database.databaseQuery("SELECT username FROM users WHERE username = ?;", username);
        if (rs != null && rs.next()) {
            rs.close();
            return false;
        }
        return true;
    }
}
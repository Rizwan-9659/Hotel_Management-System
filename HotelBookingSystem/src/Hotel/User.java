package Hotel;

import java.sql.*;

public class User {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String password;

    // Constructor for new user registration
    public User(String username, String email, String phone, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Constructor for existing user from DB
    public User(int id, String username, String email, String phone, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // ---------------- Save user to DB ----------------
    public User saveToDB() {
        try (Connection con = DBUtil.getConnection()) {
            // Check if user already exists
            String checkSql = "SELECT * FROM tbl_users WHERE username=? OR email=?";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // User already exists, load info
                this.id = rs.getInt("user_id"); // match DB column
                this.username = rs.getString("username");
                this.email = rs.getString("email");
                this.phone = rs.getString("phone");
                this.password = rs.getString("password");
                return this;
            }

            // Insert new user
            String insertSql = "INSERT INTO tbl_users(username,email,phone,password) VALUES(?,?,?,?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, phone);
            insertStmt.setString(4, password);
            insertStmt.executeUpdate();

            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) this.id = keys.getInt(1); // get auto-generated user_id

            System.out.println("✅ User saved successfully! ID: " + id);
            return this;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------- Login ----------------
    public static User login(String uname, String pass) {
        try (Connection con = DBUtil.getConnection()) {
            String sql = "SELECT * FROM tbl_users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, uname);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),   // match DB column
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password")
                );
            }

            return null; // invalid credentials

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

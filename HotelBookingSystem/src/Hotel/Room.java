package Hotel;

import java.sql.*;
import java.util.List;

public class Room {
    private int roomNumber;
    private String type;
    private double price;
    private boolean available;

    // Constructor
    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.available = true; // default available
    }

    // Getters
    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }

    // -------- Save Room to DB --------
    public void saveToDB() {
        try (Connection con = DBUtil.getConnection()) {
            String sql = "INSERT INTO tbl_rooms (room_number, type, price, available) VALUES (?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE type=?, price=?, available=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, roomNumber);
                ps.setString(2, type);
                ps.setDouble(3, price);
                ps.setBoolean(4, available);
                ps.setString(5, type);
                ps.setDouble(6, price);
                ps.setBoolean(7, available);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------- Load Rooms from DB --------
    public static void loadRoomsFromDB(List<Room> rooms) {
        rooms.clear();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tbl_rooms")) {

            while (rs.next()) {
                Room r = new Room(
                    rs.getInt("room_number"),
                    rs.getString("type"),
                    rs.getDouble("price")
                );
                r.available = rs.getBoolean("available");
                rooms.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------- Book Room (set available = false) --------
    public void bookRoom() {
        this.available = false;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tbl_rooms SET available = 0 WHERE room_number = ?")) {
            ps.setInt(1, roomNumber);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------- Reset All Rooms to Available --------
    public static void makeAllRoomsAvailable() {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tbl_rooms SET available = 1")) {
            int updated = ps.executeUpdate();
            System.out.println("✅ " + updated + " rooms reset to available.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------- toString() --------
    @Override
    public String toString() {
        return "Room " + roomNumber + " [" + type + "] - ₹" + price + " - " +
               (available ? "Available" : "Booked");
    }
}

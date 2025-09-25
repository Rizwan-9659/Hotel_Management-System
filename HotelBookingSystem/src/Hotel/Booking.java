package Hotel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

public class Booking {
    private User user;
    private Room room;
    private LocalDateTime bookingDate;

    public Booking(User user, Room room) {
        this.user = user;
        this.room = room;
        this.bookingDate = LocalDateTime.now();
    }

    public void generateReceipt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String receipt = "----- Booking Receipt -----\n" +
                "User: " + user.getUsername() + "\n" +
                "Room: " + room + "\n" +
                "Booking Date: " + bookingDate.format(formatter) + "\n" +
                "---------------------------\n";

        System.out.println(receipt);

        try (FileWriter writer = new FileWriter("receipt_" + user.getUsername() + ".txt")) {
            writer.write(receipt);
            System.out.println("📄 Receipt saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }

    public void saveToDB() {
        String sql = "INSERT INTO tbl_bookings(user_id, room_number, booking_date) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, room.getRoomNumber());
            ps.setTimestamp(3, Timestamp.valueOf(bookingDate));
            ps.executeUpdate();

            System.out.println("✅ Booking saved to database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error saving booking to DB: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Booking: " + user.getUsername() + " -> Room " + room.getRoomNumber() +
                " on " + bookingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

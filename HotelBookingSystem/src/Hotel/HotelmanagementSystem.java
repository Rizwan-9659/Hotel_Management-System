package Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelmanagementSystem {
    static Scanner sc = new Scanner(System.in);
    static List<Room> rooms = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();

    // ANSI Colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        // Pre-populate rooms (only if they don't already exist in DB)
        addRoomIfNotExists(101, "Single", 1500);
        addRoomIfNotExists(102, "Single", 1500);
        addRoomIfNotExists(201, "Double", 2500);
        addRoomIfNotExists(202, "Double", 2500);
        addRoomIfNotExists(301, "Suite", 4000);

        // Load rooms from database
        Room.loadRoomsFromDB(rooms);

        while (true) {
            System.out.println(CYAN + "\n🏨 Hotel Management System" + RESET);
            System.out.println(YELLOW + "1. Admin Login" + RESET);
            System.out.println(YELLOW + "2. User Menu" + RESET);
            System.out.println(YELLOW + "3. Exit" + RESET);
            System.out.print(GREEN + "Enter choice: " + RESET);
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: adminLogin(); break;
                case 2: userMenu(); break;
                case 3: System.out.println(GREEN + "👋 Exiting... Thank you!" + RESET); return;
                default: System.out.println(RED + "❌ Invalid choice!" + RESET);
            }
        }
    }

    // ---------------- Admin Login ----------------
    private static void adminLogin() {
        System.out.print("Enter admin username: ");
        String u = sc.nextLine();
        System.out.print("Enter admin password: ");
        String p = sc.nextLine();

        if (u.equals("admin") && p.equals("admin123")) {
            System.out.println(GREEN + "✅ Admin logged in." + RESET);

            System.out.println(CYAN + "\nAll Rooms:" + RESET);
            for (Room r : rooms) System.out.println(r);

            System.out.println(CYAN + "\nAll Bookings:" + RESET);
            for (Booking b : bookings) System.out.println(b);
        } else {
            System.out.println(RED + "❌ Invalid admin credentials." + RESET);
        }
    }

    // ---------------- User Menu ----------------
    private static void userMenu() {
        while (true) {
            System.out.println("\n" + YELLOW + "1. Register" + RESET);
            System.out.println(YELLOW + "2. Login" + RESET);
            System.out.println(YELLOW + "3. Back to Main Menu" + RESET);
            System.out.print(GREEN + "Enter choice: " + RESET);
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: registerUser(); break;
                case 2: loginUser(); break;
                case 3: return;
                default: System.out.println(RED + "❌ Invalid option." + RESET);
            }
        }
    }

    // ---------------- User Registration ----------------
    private static void registerUser() {
        System.out.print("Enter username: "); 
        String uname = sc.nextLine();
        System.out.print("Enter email: "); 
        String email = sc.nextLine();
        System.out.print("Enter phone: "); 
        String phone = sc.nextLine();
        System.out.print("Enter password: "); 
        String pass = sc.nextLine();

        User user = new User(uname, email, phone, pass).saveToDB();
        if (user != null) {
            System.out.println(GREEN + "✅ User registered successfully! User ID: " + user.getId() + RESET);
            loggedInMenu(user);
        } else {
            System.out.println(RED + "❌ Registration failed." + RESET);
        }
    }

    // ---------------- User Login ----------------
    private static void loginUser() {
        System.out.print("Enter username: "); 
        String uname = sc.nextLine();
        System.out.print("Enter password: "); 
        String pass = sc.nextLine();

        User user = User.login(uname, pass);
        if (user != null) {
            System.out.println(GREEN + "✅ Login successful! Welcome " + user.getUsername() + RESET);
            loggedInMenu(user);
        } else {
            System.out.println(RED + "❌ Invalid username or password." + RESET);
        }
    }

    // ---------------- Logged-in User Menu ----------------
    private static void loggedInMenu(User user) {
        while (true) {
            System.out.println("\n" + YELLOW + "1. View Available Rooms" + RESET);
            System.out.println(YELLOW + "2. Book a Room" + RESET);
            System.out.println(YELLOW + "3. Logout" + RESET);
            System.out.print(GREEN + "Enter choice: " + RESET);
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.println(CYAN + "\nAvailable Rooms:" + RESET);
                    for (Room r : rooms) 
                        if (r.isAvailable()) System.out.println(r);
                    break;

                case 2:
                    System.out.print("Enter room number to book: ");
                    int rno = sc.nextInt();
                    boolean booked = false;
                    for (Room r : rooms) {
                        if (r.getRoomNumber() == rno && r.isAvailable()) {
                            r.bookRoom();               // updates room availability in DB
                            Booking booking = new Booking(user, r);
                            booking.saveToDB();         // save booking in DB
                            bookings.add(booking);      // add to list
                            booking.generateReceipt();  // create receipt
                            booked = true;
                            break;
                        }
                    }
                    if (!booked) System.out.println(RED + "❌ Room not available." + RESET);
                    break;

                case 3: return;

                default: System.out.println(RED + "❌ Invalid option." + RESET);
            }
        }
    }

    // ---------------- Pre-populate Rooms ----------------
    private static void addRoomIfNotExists(int roomNumber, String type, double price) {
        // Avoid duplicate in DB
        boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == roomNumber);
        if (!exists) {
            Room room = new Room(roomNumber, type, price);
            room.saveToDB(); // saves room to MySQL
        }
    }
}

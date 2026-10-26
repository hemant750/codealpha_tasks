
import java.util.*;
import java.io.*;

class Room {
    int roomNumber;
    String type;
    double price;
    boolean booked;

    Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.booked = false;
    }
}

class Booking {
    int bookingId;
    String customerName;
    int roomNumber;
    int days;
    double bill;

    Booking(int bookingId, String customerName,
            int roomNumber, int days, double bill) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.days = days;
        this.bill = bill;
    }
}

public class HotelReservationSystem {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static int nextBookingId = 1001;

    static final String FILE_NAME = "hotel_bookings.txt";

    public static void main(String[] args) {
        initializeRooms();
        loadBookings();

        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View All Rooms");
            System.out.println("2. View Available Rooms");
            System.out.println("3. Book a Room");
            System.out.println("4. View All Bookings");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    viewRooms(false);
                    break;
                case 2:
                    viewRooms(true);
                    break;
                case 3:
                    bookRoom();
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    cancelBooking();
                    break;
                case 6:
                    saveBookings();
                    System.out.println("Thank you for using our system!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);

        sc.close();
    }

    static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    static void initializeRooms() {
        rooms.add(new Room(101, "Single", 1000));
        rooms.add(new Room(102, "Single", 1000));
        rooms.add(new Room(201, "Double", 1800));
        rooms.add(new Room(202, "Double", 1800));
        rooms.add(new Room(301, "Deluxe", 3000));
        rooms.add(new Room(302, "Deluxe", 3000));
    }

    static Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.roomNumber == roomNumber) {
                return room;
            }
        }
        return null;
    }

    static void viewRooms(boolean availableOnly) {
        System.out.println("\n--- ROOM DETAILS ---");
        System.out.printf("%-12s %-12s %-12s %-12s%n",
                "Room No.", "Type", "Price/Day", "Status");

        for (Room room : rooms) {
            if (availableOnly && room.booked) {
                continue;
            }

            System.out.printf("%-12d %-12s Rs.%-9.2f %-12s%n",
                    room.roomNumber, room.type, room.price,
                    room.booked ? "Booked" : "Available");
        }
    }

    static void bookRoom() {
        viewRooms(true);

        System.out.print("\nEnter room number: ");
        int roomNumber = readInt();

        Room room = findRoom(roomNumber);

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        if (room.booked) {
            System.out.println("Sorry, this room is already booked.");
            return;
        }

        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Invalid name. Please try again.");
            return;
        }

        System.out.print("Enter number of days: ");
        int days = readInt();

        if (days <= 0) {
            System.out.println("Days must be greater than zero.");
            return;
        }

        double bill = room.price * days;

        Booking booking = new Booking(
                nextBookingId++, name, roomNumber, days, bill);

        bookings.add(booking);
        room.booked = true;
        saveBookings();

        System.out.println("\nBooking successful!");
        System.out.println("Booking ID: " + booking.bookingId);
        System.out.println("Customer: " + booking.customerName);
        System.out.println("Room: " + booking.roomNumber);
        System.out.println("Total bill: Rs." + booking.bill);
    }

    static void viewBookings() {
        System.out.println("\n--- ALL BOOKINGS ---");

        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("------------------------------");
            System.out.println("Booking ID: " + booking.bookingId);
            System.out.println("Customer: " + booking.customerName);
            System.out.println("Room Number: " + booking.roomNumber);
            System.out.println("Days: " + booking.days);
            System.out.printf("Total Bill: Rs.%.2f%n", booking.bill);
        }
    }

    static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = readInt();

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);

            if (booking.bookingId == bookingId) {
                Room room = findRoom(booking.roomNumber);

                if (room != null) {
                    room.booked = false;
                }

                bookings.remove(i);
                saveBookings();

                System.out.println("Booking cancelled successfully.");
                return;
            }
        }

        System.out.println("Booking ID not found.");
    }

    static void saveBookings() {
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Booking booking : bookings) {
                writer.println(
                    booking.bookingId + "|" +
                    booking.customerName + "|" +
                    booking.roomNumber + "|" +
                    booking.days + "|" +
                    booking.bill
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    static void loadBookings() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length != 5) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int roomNumber = Integer.parseInt(data[2]);
                    int days = Integer.parseInt(data[3]);
                    double bill = Double.parseDouble(data[4]);

                    Room room = findRoom(roomNumber);

                    if (room != null && !room.booked) {
                        bookings.add(new Booking(
                                id, name, roomNumber, days, bill));
                        room.booked = true;
                        nextBookingId = Math.max(nextBookingId, id + 1);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid booking record.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }
}

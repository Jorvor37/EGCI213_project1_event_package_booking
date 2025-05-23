package Project1_6680081;

import java.io.*;
import java.util.*;

/*
  @author Kongphop Kayoonvihcien      ID: 6680081
          Wasupon Wiriyakitanan       ID: 6680646
          Phattarada Limsuchaiwat     ID: 6680684
          Kasidit Boonluar            ID: 6680028
*/

// Abstract class Item
abstract class Item {
    protected String code;
    protected String name;
    protected double unitPrice;

    public Item(String code, String name, double unitPrice){
        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
}

// Room subclass
class Room extends Item {
    public Room(String code, String name, double unitPrice) {
        super(code, name, unitPrice);
    }

    public double calPrice(int quantity, int days, int persons) {
        double basePrice = unitPrice * quantity * days;
        double priceWithService = basePrice * 1.1;  // +10% service charge
        double priceWithVAT = priceWithService * 1.07; // +7% VAT
        return priceWithVAT;
    }
}

// Meal subclass
class Meal extends Item {
    public Meal(String code, String name, double unitPrice) {
        super(code, name, unitPrice);
    }

    public double calPrice(int quantity, int days, int persons) {
        return unitPrice * quantity * persons * days; // VAT included already assumed
    }
}

// Discount criterion class
class DiscountCriterion {
    private int minSubtotal;
    private double discountRate;

    public DiscountCriterion(int minSubtotal, double discountRate) {
        this.minSubtotal = minSubtotal;
        this.discountRate = discountRate;
    }

    public int getMinSubtotal() { return minSubtotal; }
    public double getDiscountRate() { return discountRate; }

    // Static method to find best discount rate based on subtotal
    public static double getBestDiscount(double price, ArrayList<DiscountCriterion> discountList) {
    double highestDiscount = 0.0;

    for (DiscountCriterion discount : discountList) {
        int minimumAmount = discount.getMinSubtotal();
        double discountRate = discount.getDiscountRate();

        if (price >= minimumAmount && discountRate > highestDiscount) {
            highestDiscount = discountRate;
        }
    }

    return highestDiscount;
}


    // Load discounts from file
    public static ArrayList<DiscountCriterion> loadDiscounts(String filename) {
        ArrayList<DiscountCriterion> discounts = new ArrayList<>();
        try (Scanner scan = new Scanner(new File(filename))) {
            if (scan.hasNextLine()) scan.nextLine(); // Skip header
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length < 2) {
                    System.err.println("Invalid discount line: " + line);
                    continue;
                }
                int minSubtotal = Integer.parseInt(cols[0].trim());
                double rate = Double.parseDouble(cols[1].trim()) / 100.0; // convert percent to fraction
                discounts.add(new DiscountCriterion(minSubtotal, rate));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Discount file not found: " + filename);
        }
        return discounts;
    }
}

// Booking class
class Booking {
    private String bookingId;
    private String customerId;
    private int days;
    private int[] roomCount; // number of rooms per type
    private int persons;
    private int[] mealCount; // number of meals per type

    private double roomTotal;
    private double mealTotal;
    private double subTotal;
    private double discountAmount;
    private double totalAfterDiscount;

    public Booking(String bookingId, String customerId, int days, int[] roomCount, int persons, int[] mealCount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.days = days;
        this.roomCount = roomCount;
        this.persons = persons;
        this.mealCount = mealCount;
    }

    public void calculateTotal(Room[] rooms, Meal[] meals, ArrayList<DiscountCriterion> discounts) {
        roomTotal = 0;
        for (int i = 0; i < rooms.length; i++) {
            roomTotal += rooms[i].calPrice(roomCount[i], days, persons);
        }

        mealTotal = 0;
        for (int i = 0; i < meals.length; i++) {
            mealTotal += meals[i].calPrice(mealCount[i], days, persons);
        }

        subTotal = roomTotal + mealTotal;
        double discountRate = DiscountCriterion.getBestDiscount(subTotal, discounts);
        discountAmount = subTotal * discountRate;
        totalAfterDiscount = subTotal - discountAmount;
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public double getSubTotal() { return subTotal; }
    public double getTotalAfterDiscount() { return totalAfterDiscount; }
    public double getDiscountAmount() { return discountAmount; }
}

// Customer class to store bookings per customer
class Customer {
    private String customerId;
    private ArrayList<Booking> bookings = new ArrayList<>();

    public Customer(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }

    public void addBooking(Booking b) {
        bookings.add(b);
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    // Sum of all booking subtotals (before discount)
    public double getTotalBookingAmount() {
        double total = 0;
        for (Booking b : bookings) {
            total += b.getSubTotal();
        }
        return total;
    }
}

// Main class
public class w4_project1 {

    public static void main(String[] args) {
        String path = "src/main/java/Project1_6680081/";

        String inBookings = path + "bookings.txt";
        String inDiscounts = path + "discounts.txt";
        String inItems = path + "items.txt";

        // Load discounts
        ArrayList<DiscountCriterion> discounts = DiscountCriterion.loadDiscounts(inDiscounts);

        // Load items (Rooms and Meals)
        Room[] rooms = new Room[3];
        Meal[] meals = new Meal[3];
        try (Scanner scan = new Scanner(new File(inItems))) {
            if (scan.hasNextLine()) scan.nextLine(); // Skip header
            int roomIdx = 0, mealIdx = 0;
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length < 4) {
                    System.err.println("Invalid item line: " + line);
                    continue;
                }
                String type = cols[0].trim().toLowerCase();
                String code = cols[1].trim();
                String name = cols[2].trim();
                double price = Double.parseDouble(cols[3].trim());

                if (type.equals("room")) {
                    if (roomIdx < rooms.length)
                        rooms[roomIdx++] = new Room(code, name, price);
                } else if (type.equals("meal")) {
                    if (mealIdx < meals.length)
                        meals[mealIdx++] = new Meal(code, name, price);
                } else {
                    System.err.println("Unknown item type: " + type);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Items file not found: " + inItems);
            return;
        }

        // Load bookings and process
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Scanner scan = new Scanner(new File(inBookings))) {
            if (scan.hasNextLine()) scan.nextLine(); // Skip header

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 6) throw new Exception("Missing values");

                    String bookingId = parts[0].trim();
                    String customerId = parts[1].trim();
                    int days = Integer.parseInt(parts[2].trim());

                    String[] roomParts = parts[3].trim().split(":");
                    if (roomParts.length != 3) throw new Exception("Invalid room format");
                    int[] roomCount = new int[3];
                    for (int i = 0; i < 3; i++) {
                        roomCount[i] = Integer.parseInt(roomParts[i].trim());
                    }

                    int persons = Integer.parseInt(parts[4].trim());

                    String[] mealParts = parts[5].trim().split(":");
                    if (mealParts.length != 3) throw new Exception("Invalid meal format");
                    int[] mealCount = new int[3];
                    for (int i = 0; i < 3; i++) {
                        mealCount[i] = Integer.parseInt(mealParts[i].trim());
                    }

                    Booking b = new Booking(bookingId, customerId, days, roomCount, persons, mealCount);
                    b.calculateTotal(rooms, meals, discounts);
                    bookings.add(b);

                } catch (Exception e) {
                    System.err.println("Skipping invalid booking line: " + line + " (" + e.getMessage() + ")");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Bookings file not found: " + inBookings);
            return;
        }

        // Group bookings by customer
        Map<String, Customer> customers = new HashMap<>();
        for (Booking b : bookings) {
            customers.putIfAbsent(b.getCustomerId(), new Customer(b.getCustomerId()));
            customers.get(b.getCustomerId()).addBooking(b);
        }

        // Sort customers by total booking amount desc, then by customer ID asc
        List<Customer> sortedCustomers = new ArrayList<>(customers.values());
        sortedCustomers.sort((c1, c2) -> {
            int cmp = Double.compare(c2.getTotalBookingAmount(), c1.getTotalBookingAmount());
            if (cmp != 0) return cmp;
            return c1.getCustomerId().compareTo(c2.getCustomerId());
        });

        // Print booking details
        System.out.println("Booking details:");
        for (Booking b : bookings) {
            System.out.printf("Booking ID: %s, Customer ID: %s, Subtotal: %.2f, Discount: %.2f, Total After Discount: %.2f\n",
                    b.getBookingId(), b.getCustomerId(), b.getSubTotal(), b.getDiscountAmount(), b.getTotalAfterDiscount());
        }

        System.out.println("\nCustomer summary:");
        // Print summary report per customer
        for (Customer c : sortedCustomers) {
            System.out.printf("Customer ID: %s, Total Booking Amount: %.2f, Bookings: ",
                    c.getCustomerId(), c.getTotalBookingAmount());
            List<String> bookingIds = new ArrayList<>();
            for (Booking b : c.getBookings()) {
                bookingIds.add(b.getBookingId());
            }
            System.out.println(String.join(", ", bookingIds));
        }
    }
}

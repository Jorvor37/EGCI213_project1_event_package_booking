package Project1_6680081;

/*
    @author Kongphop Kayoonvihcien      ID: 6680081
            Wasupon Wiriyakitanan       ID: 6680646
            Phattarada Limsuchaiwat     ID: 6680684
            Kasidit Boonluar            ID: 6680028
*/

import java.io.*;
import java.util.*;

public class w4_project1 {
    public static void main(String[] args){
        String path = "src/main/java/Project1_6680081/"; 
        
        String inItems      = path + "items.txt";       //read Items
        String inDiscounts  = path + "discounts.txt";   //read Discounts
        String inBookings   = path + "bookings.txt";    //read Bookings

        ArrayList<booking> bookings = new ArrayList<>();
        ArrayList<DiscountCriterion> discounts = new ArrayList<>();

        Room[] rooms = new Room[3]; 
        Meal[] meals = new Meal[3];

////////////////////////////////////////////////////////////////////////////
       // Read items.txt
        try (Scanner scan = new Scanner(new File(inItems))) {
            if (scan.hasNextLine()) scan.nextLine(); // skip header line
            
            int roomIdx = 0, mealIdx = 0;
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                //error handler
                if (parts.length < 3) {
                    System.err.println("Invalid item line: " + line);
                    continue;
                }
                
                //store data code, name, price
                String code = parts[0].trim();
                String name = parts[1].trim();
                double unitPrice = Double.parseDouble(parts[2].trim());
                
                if (code.startsWith("R")) {
                    if (roomIdx < rooms.length) {
                        rooms[roomIdx++] = new Room(code, name, unitPrice);
                    }
                } else if (code.startsWith("M")) {
                    if (mealIdx < meals.length) {
                        meals[mealIdx++] = new Meal(code, name, unitPrice);
                    }
                } else {
                    System.err.println("Unknown item code prefix: " + code);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Items file not found: " + inItems);
            // You might want to exit or handle this scenario gracefully
        }

////////////////////////////////////////////////////////////////////////////
       // Read discounts.txt
        try (Scanner scan = new Scanner(new File(inDiscounts))) {
            if (scan.hasNextLine()) scan.nextLine(); // skip header line

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Invalid discount line: " + line);
                    continue;
                }

                int minSubtotal = Integer.parseInt(parts[0].trim());
                double discountRate = Double.parseDouble(parts[1].trim());

                discounts.add(new DiscountCriterion(minSubtotal, discountRate));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Discount file not found: " + inDiscounts);
        }

////////////////////////////////////////////////////////////////////////////
        //read bookings.txt

        
        try (Scanner scan = new Scanner(new File(inBookings))) {
            if(scan.hasNextLine()) scan.nextLine();
            
            while(scan.hasNextLine())
            {
                String line = scan.nextLine(); 
                if(line.isEmpty()) continue;
                
                try{
                    String[] part = line.split(",");
                    if (part.length < 6) throw new Exception ("Missing values");
                    
                    //general information
                    String bookingid = part[0].trim();
                    String customerid = part[1].trim();
                    int days = Integer.parseInt(part[2].trim());
                    
                    //rooms
                    String[] roomparts = part[3].trim().split(":");
                    if (roomparts.length != 3) throw new Exception ("Invalid room format");
                    int[] room_count = new int[3];
                    for (int i = 0; i < 3; i++)
                    {
                        room_count[i] = Integer.parseInt(roomparts[i].trim());
                    }
                    int persons = Integer.parseInt(part[4].trim());
                    
                    //meals
                    String[] mealparts = part[5].trim().split(":");
                    if (mealparts.length != 3) throw new Exception ("Invalid meal format");
                    int[] meal_count = new int[3];
                    for (int i = 0; i < 3; i++)
                    {
                        meal_count[i] = Integer.parseInt(mealparts[i].trim());
                    }
                    
                    //create
                    Booking book = new Booking(bookingid, customerid, days, room_count, persons, meal_count);
                    book.calculation_Total(rooms, meals, discounts);
                    bookings.add(book);
                
                }catch (Exception e){
                    System.err.println("Skipping invalid line: " + line);
                }
            }
        }catch (FileNotFoundException e){
            System.err.println("File not found: " + inBookings);
            //return;
        }

////////////////////////////////////////////////////////////////////////////+
        // Sotring and printing TEST
        Map<String, Customer> customers = new HashMap<>();

        for (Booking b : bookings) {
            customers.putIfAbsent(b.getCustomerId(), new Customer(b.getCustomerId()));
            customers.get(b.getCustomerId()).addBooking(b);
        }

        List<Customer> sortedCustomers = new ArrayList<>(customers.values());
        sortedCustomers.sort((c1, c2) -> {
            int cmp = Double.compare(c2.getTotalBookingAmount(), c1.getTotalBookingAmount());
            if (cmp != 0) return cmp;
            return c1.getCustomerId().compareTo(c2.getCustomerId());
        });

        for (Customer c : sortedCustomers) {
            System.out.printf("Customer %s total booking amount: %.2f\n", c.getCustomerId(), c.getTotalBookingAmount());
            System.out.print("Bookings: ");
            for (Booking b : c.getBookings()) {
                System.out.print(b.getBookingId() + " ");
            }
            System.out.println("\n");
        }


    } //end of main
} //end of w4_project1


////////////////////////////////////////////////////////////////////////////
///use abstract to be structure of item
///Implement class item [1. DONE]
abstract class Item {
    protected String code;
    protected String name;
    protected double unitPrice;

    //constructor
    public Item(String code, String name, double unitPrice){
        //copies the values from parameters into the object’s fields.
        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    //get protected data
    public String getCode()         {return code;}
    public String getName()         {return name;}
    public double getUnitPrice()    {return unitPrice;}
}

////////////////////////////////////////////////////////////////////////////
///sub class room and meal are sub class of Item
class Room extends Item {
    //constructor refers to Item class
    public Room(String code, String name, double unitPrice) {
        super(code, name, unitPrice);
    }

    //calculate price function, add 10% service charge, and 7% VAT
    public double calPrice(int quantity, int days, int persons){
        double basePrice = unitPrice * quantity * days;
        double pricewService = basePrice + (basePrice*0.1);
        double pricewVAT = pricewService + (pricewService*0.07);
        return pricewVAT;
    }
}

class Meal extends Item {
    //constructor refers to Item class
    public Meal(String code, String name, double unitPrice) {
        super(code, name, unitPrice);
    }

    //calculate price function VAT included
    public double calPrice(int quantity, int days, int persons) {
        return unitPrice * quantity * persons * days;
    }
}

////////////////////////////////////////////////////////////////////////////
/// Customer class to store bookings per customer [2. DONE]
class Customer {
    private String customerId;
    private List<Booking> bookings = new ArrayList<>();

    public Customer(String customerId)              {this.customerId = customerId;}
    public void addBooking(Booking booking)         {bookings.add(booking);}
    public String getCustomerId()                   {return customerId;}
    public List<Booking> getBookings()              {return bookings;}
    public double getTotalBookingAmount() {
        double total = 0;
        for (Booking b : bookings) {
            total += b.getSubTotal();
        }
        return total;
    }
}

////////////////////////////////////////////////////////////////////////////
/// Booking class [3. DONE]
class Booking {
    private String bookingId;
    private String customerId;
    private int days;
    private int[] roomCount;
    private int persons;
    private int[] mealCount;

    private double roomTotal;
    private double mealTotal;
    private double subTotal;
    private double discountAmount;
    private double totalAfterDiscount;

    //constructor
    public Booking(String bookingId, String customerId, int days, int[] roomCount, int persons, int[] mealCount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.days = days;
        this.roomCount = roomCount;
        this.persons = persons;
        this.mealCount = mealCount;
    }

    public void calculateTotal(Room[] rooms, Meal[] meals, List<DiscountCriterion> discounts) {

        //calRoom [3.1 DONE]
        roomTotal = 0;
        for (int i = 0; i < rooms.length; i++) {
            roomTotal += rooms[i].calPrice(roomCount[i], days, 1);
        }

        //calMeal [3.2 DONE]
        mealTotal = 0;
        for (int i = 0; i < meals.length; i++) {
            mealTotal += meals[i].calPrice(mealCount[i], days, persons);
        }

        //subTotal [3.3 DONE]
        subTotal = roomTotal + mealTotal;

        //Discount [3.4 DONE]
        double discountRate = DiscountCriterion.getBestDiscountRate(subTotal, discounts);
        discountAmount = subTotal * (discountRate / 100.0);  // discount is percent
        totalAfterDiscount = subTotal - discountAmount;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public double getSubTotal() { return subTotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalAfterDiscount() { return totalAfterDiscount; }
    }

////////////////////////////////////////////////////////////////////////////
/// Discount class [4. DONE]
class DiscountCriterion {
    private int minSubtotal;
    private double discountRate;

    public DiscountCriterion(int minSubtotal, double discountRate) {
        this.minSubtotal = minSubtotal;
        this.discountRate = discountRate;
    }

    public int getMinSubtotal() { return minSubtotal; }
    public double getDiscountRate() { return discountRate; }

    public static double getBestDiscountRate(double price, List<DiscountCriterion> discounts) {
        double best = 0;
        for (DiscountCriterion d : discounts) {
            if (price >= d.getMinSubtotal() && d.getDiscountRate() > best) {
                best = d.getDiscountRate();
            }
        }
        return best;
    }
}
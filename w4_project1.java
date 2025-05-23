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
        
        
        String inBookings   = path + "bookings.txt";    //read Bookings
        String inDiscounts  = path + "discounts.txt";   //read Discounts
        String inItems      = path + "items.txt";       //read Items
        /*
        System.out.println("Read from "+path+"bookings.txt");
        System.out.println("Read from "+path+"discounts.txt");
        */

       ArrayList<booking> bookings = new ArrayList<>();
        Room[] rooms = new Room[3]; 
        Meal[] meals = new Meal[3];
        
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
                    booking book = new booking(bookingid, customerid, days, room_count, persons, meal_count);
                    book.calculation_Total(rooms, meals, discounts);
                    bookings.add(b);
                
                }catch (Exception e){
                    System.err.println("Skipping invalid line: " + line);
                }
            }
            scan.close();
        }catch (FileNotFoundException e){
            System.err.println("File not found: " + inBookings);
            //return;
        }

    }
}

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

class booking
{
    private String bookingid;
    private String customerid;
    private int days;
    private int[] room_count;
    private int persons;
    private int[] meal_count;
        
    private double roomTotal;
    private double mealTotal;
    private double subTotal; //room + meal
    private double discount;
    private double Total_after_discount;
    
    public booking(String bookingid, String customerid, int days, int[] room_count, int persons, int[] meal_count) //constructor
    {
        this.bookingid = bookingid;
        this.customerid = customerid;
        this.days = days;
        this.room_count = room_count;
        this.persons = persons;
        this.meal_count = meal_count;
    }
    
    public void calculation_Total(Room[] rooms, Meal[] meals, ArrayList<DiscountCriterion> discounts)
    {
        roomTotal = 0;
        for(int i = 0; i < 3; i++)
        {
            //double calPrice(int quantity, int days, int persons) but person here not use
            roomTotal += rooms[i].calPrice(room_count[i], days, 1);
        }
           
        mealTotal = 0;
        for(int i = 0; i < 3; i++)
        {
            //double calPrice(int quantity, int days, int persons)
            mealTotal += meals[i].calPrice(meal_count[i], days, persons);
        }
        
        subTotal = roomTotal + mealTotal;
        discount = DiscountCriterion.getBestDiscount(subTotal, discounts);
        Total_after_discount = subTotal * (1 - discount / 100.0);
        }
    
    public String getid()                       {return bookingid;}
    public String getcustomerid()               {return customerid;}
    public double getsubTotal()                 {return subTotal;}
    public double getTotal_after_discount()     {return Total_after_discount;}
}
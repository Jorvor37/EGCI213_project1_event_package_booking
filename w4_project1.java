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
        
        /*
        String inBookings   = path + "bookings.txt";    //read Bookings
        String inDiscounts  = path + "discounts.txt";   //read Discounts
        String inItems      = path + "items.txt";       //read Items
        System.out.println("Read input from "+path+"bookings.txt");
        System.out.println("Read input from "+path+"discounts.txt");
        System.out.println("Read input from "+path+"items.txt");
        */
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///use abstract to be structure of item
    ///Implement class item [DONE]
    public abstract class Item {
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
    public class Room extends Item {
        //constructor refers to Item class
        public Room(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
        
        //calculate price function, add 10% service charge, and 7% VAT
        public double calPrice(int quantity, int days, int person){
            double basePrice = unitPrice * quantity * days;
            double pricewService = basePrice + (basePrice*0.1);
            double pricewVAT = pricewService + (pricewService*0.07);
            return pricewVAT;
        }
    }
    
    public class Meal extends Item {
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
    /// Booking class
    public class booking
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
           //room
           roomTotal = 0;
           for(int i = 0; i < 3; i++){
               roomTotal += rooms[i].calPrice(room_count[i], days, persons);
           }
           
           //meal
           mealTotal = 0;
           for(int i = 0; i < 3; i++){
               mealTotal += meals[i].calPrice(meal_count[i], days, persons);
           }
           
           subTotal = roomTotal + mealTotal;
           
           discount = DiscountCriterion.getBestDiscount(subTotal, discounts);
           
           Total_after_discount = subTotal * (1 - discount / 100.0);
        }
       
        public static ArrayList<booking> readfromFile (String filename, Room[] rooms, Meal[] meals, ArrayList<DiscountCriterion> discounts)
        {
            ArrayList<booking> bookings = new ArrayList<>();
            try{
                File infile = new File(filename);
                Scanner scan = new Scanner(infile);
               
                if(scan.hasNextLine()) scan.nextLine();
                while(scan.hasNextLine())
                {
                    String line = scan.nextLine();
                    if(line.isEmpty()) continue;
                   
                    try{
                        String[] parts = line.split(",");
                        if (parts.length < 6) throw new Exception ("Missing values");
                       
                        String bookingid = parts[0].trim();
                        String customerid = parts[1].trim();
                        int days = Integer.parseInt(parts[2].trim());
                        
                        int[] room_count = Array.stream(parts[3].trim()).split(":");
                        int persons = Integer.parseInt(parts[4].trim());
                        int[] meal_count = Array.stream(parts[5].trim().split(":"));
                       
                        booking b = new booking(bookingid, customerid, days, room_count, persons, meal_count);
                        b.calculation_Total(rooms, meals, discounts);
                        bookings.add(b);
                    }catch (Exception e){
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
                scan.close();
            }catch (FileNotFoundException e){
                System.err.println("File not found: " + filename);
            }
            return bookings;
        }
        public String getid()
        {
            return bookingid;
        }
       
        public String getcustomerid()
        {
            return customerid;
        }
       
        public double getsubTotal()
        {
            return subTotal;
        }
       
        public double getTotal_after_discount()
        {
            return Total_after_discount;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///Boss
    public class Discount{
        
    }
}

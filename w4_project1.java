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

    }
}

////////////////////////////////////////////////////////////////////////////
///use abstract to be structure of item
///Implement class item [DONE]
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
    public double calPrice(int quantity, int days, int person){
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

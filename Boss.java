/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Project_1;
import java.util.*;
import java.io.*;

/**
 *
 * @author boss
 */
public class Project1 {

    public static class discountcriterion{
        private String filename;
        int min_subtotal;
        double discountrate;
        ArrayList<discountcriterion> discounts;
        private List<discountcriterion> discountrules;
       
    public discountcriterion(int min_subtotal, double discountrate){
        this.min_subtotal = min_subtotal;
        this.discountrate = discountrate;
        
        discountrules.add(new discountcriterion(100000,0.025));
        
    }
    
    public void readdiscountfile(String filename, ArrayList<discountcriterion> discounts){
         try{
             File infile = new File(filename);
             Scanner scan = new Scanner(infile);
             
             if(scan.hasNextLine()){
                 scan.nextLine();
             }
             
             while(scan.hasNext()){
                 String line = scan.nextLine();
                 String cols[] = line.split(",");
                 if (cols.length < 2) {
                 throw new ArrayIndexOutOfBoundsException("Missing columns: " + line);
               }
                 
                 min_subtotal = Integer.parseInt(cols[0].trim());
                 discountrate = Double.parseDouble(cols[1].trim());
                 discountcriterion list = new discountcriterion(min_subtotal,discountrate);
                 discounts.add(list);       
             }
             scan.close();
        
        }catch(FileNotFoundException e){
            System.err.println(e); 
        }
    }
   // public double calculate(){
        //ต้องอยู่ในclassJan//
   // }
    public double GetDiscount(double price)
    {
        double bestrate = 0;
        for (discountcriterion rule : discountrules) {
    if (price >= rule.min_subtotal && rule.discountrate > bestrate)
    {
        bestrate = rule.discountrate;
    }
    }
     return price*bestrate;       
}
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String path = "src/main/java/Project1/"; 
        String inBookings   = path + "bookings.txt";    //read Bookings
        String inDiscounts  = path + "discounts.txt";   //read Discounts
        String inItems      = path + "items.txt";       //read Items
        System.out.println("Read input from "+path+"countries.txt");
        
        
       
       

    }
    
}


    


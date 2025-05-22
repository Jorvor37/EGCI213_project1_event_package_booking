public class Jan_1 {
    
    public static void main(String[] args){
        
        String path = "src/main/java/Project1/"; 
        
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
        
        try{
            Scanner scan = new Scanner(new File(inBookings));
            if(scan.hasNextLine()) scan.nextLine();
            
            while(scan.hasNextLine())
            {
                String line = scan.nextLine(); 
                if(line.isEmpty()) continue;
                
                try{
                    String[] part = line.split(",");
                    if (part.length < 6) throw new Exception ("Missing values");
                    
                    String bookingid = part[0].trim();
                    String customerid = part[1].trim();
                    int days = Integer.parseInt(part[2].trim());
                    
                    String[] roomparts = part[3].trim().split(":");
                    if (roomparts.length != 3) throw new Exception ("Invalid room format");
                    int[] room_count = new int[3];
                    for (int i = 0; i < 3; i++)
                    {
                        room_count[i] = Integer.parseInt(roomparts[i].trim());
                    }
                    
                    int persons = Integer.parseInt(part[4].trim());
                    
                    String[] mealparts = part[5].trim().split(":");
                    if (mealparts.length != 3) throw new Exception ("Invalid meal format");
                    int[] meal_count = new int[3];
                    for (int i = 0; i < 3; i++)
                    {
                        meal_count[i] = Integer.parseInt(mealparts[i].trim());
                    }
                    
                    booking booking = new booking(bookingid, customerid, days, room_count, persons, meal_count);
                    booking.calculation_Total(rooms, meals, discounts);
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
        /*
        for (booking b : bookings) {
            System.out.printf("Booking ID: %s, Customer ID: %s, Subtotal: %.2f, Total After Discount: %.2f\n",
                b.getid(), b.getcustomerid(), b.getsubTotal(), b.getTotal_after_discount());
        }*/
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
            roomTotal += rooms[i].calPrice(room_count[i], days, 1);
        }
           
        mealTotal = 0;
        for(int i = 0; i < 3; i++)
        {
            //int Total_meals = meal_count[i] * persons * days;
            mealTotal += meals[i].calPrice(meal_count[i], days, 1);
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

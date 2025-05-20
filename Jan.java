public class Project_1 {

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
           roomTotal = 0;
           for(int i = 0; i < 3; i++)
           {
               roomTotal += rooms[i].calculation_Totalprice(room_count[i], days);
           }
           mealTotal = 0;
           for(int i = 0; i < 3; i++)
           {
               int Total_meals = meal_count[i] * persons * days;
               mealTotal += meals[i].calculate_Totalprice(Total_meals, 1);
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

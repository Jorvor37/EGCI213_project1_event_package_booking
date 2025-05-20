public static class discountcriterion{
        private String filename;
        int min_subtotal;
        double discountrate;
        ArrayList<discountcriterion> discounts;
        
       
    public discountcriterion(int min_subtotal, double discountrate){
        this.min_subtotal = min_subtotal;
        this.discountrate = discountrate;
        
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
        double discount = 0;
        
        if(price>100000&&price<=500000){
            discount = price*0.025;
    }
        else if(price>500000&&price<=1000000){
            discount = price*0.05;
        }
        else if(price>1000000){
            discount = price*0.1;
        }
        return discount;
    }
}

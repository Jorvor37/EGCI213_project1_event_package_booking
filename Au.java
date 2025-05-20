
String bookingsFile = path + "bookings.txt";

try {
    File file = new File(bookingsFile);
    if (!file.exists()) {
        System.out.println("File not found: " + bookingsFile);
        return; // stop here if file is missing
    }

    Scanner scan = new Scanner(file);
    int lineNumber = 0;

    if (scan.hasNextLine()) scan.nextLine(); // skip header

    while (scan.hasNextLine()) {
        lineNumber++;
        String line = scan.nextLine();

        if (line.trim().equals("")) continue; // skip empty line

        String[] parts = line.split(",");

        if (parts.length < 6) {
            System.out.println("Line " + lineNumber + " skipped (not enough columns)");
            continue;
        }

        try {
            String bookingID = parts[0].trim();
            String customerID = parts[1].trim();
            int days = Integer.parseInt(parts[2].trim());

            String[] roomParts = parts[3].trim().split(":");
            String[] mealParts = parts[5].trim().split(":");

            if (roomParts.length != 3 || mealParts.length != 3) {
                System.out.println("Line " + lineNumber + " skipped (bad room or meal format)");
                continue;
            }

            int r1 = Integer.parseInt(roomParts[0]);
            int r2 = Integer.parseInt(roomParts[1]);
            int r3 = Integer.parseInt(roomParts[2]);
            int persons = Integer.parseInt(parts[4].trim());
            int m1 = Integer.parseInt(mealParts[0]);
            int m2 = Integer.parseInt(mealParts[1]);
            int m3 = Integer.parseInt(mealParts[2]);

            if (days <= 0 || persons <= 0 || r1 < 0 || r2 < 0 || r3 < 0 || m1 < 0 || m2 < 0 || m3 < 0) {
                System.out.println("Line " + lineNumber + " skipped (negative or zero values)");
                continue;
            }

            // checking
            System.out.println("Booking " + bookingID + " is valid");

        } catch (Exception e) {
            System.out.println("Line " + lineNumber + " skipped (invalid number format)");
        }
    }

    scan.close();

} catch (Exception e) {
    System.out.println("Error reading file: " + e.getMessage());
}

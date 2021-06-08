import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 * INFORMATION:
 * --------------------------------------------------------------------------------------------------------------------
 * This program is a simulation of a restaurant experience. Where customers begin by having a look into the menu until
 * they pay for the bill and register for the restaurant's membership if they are willing to do so.
 *
 * This was a fun practise for coding exercise. Hence there will be many opportunities for development in the future
 * which I will look into soon.
 *
 * I hope you like the outcome of this simulation. Please enjoy! Just run the main method (click on green arrow)
 *
 * Created by: Jason Nicholas Susanto, finished on the 8th of June 2021.
 */
public class Restaurant {

    // Constant variables
    private static final int LENGTH = 50;
    private static final double DISCOUNT = 0.85;

    // Decimal formatting for the food prices
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Initializing the HashMaps needed for this program
    private final HashMap<String, Member> membership = new HashMap<>();
    private final HashMap<String, Double> order = new HashMap<>();
    private final HashMap<String, Double> menu = new HashMap<>();

    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private double grandTotal=0;
    private double discountedGrandTotal=0;
    private boolean discount=false;

    /**
     * Constructor for this class
     */
    public Restaurant(){
        this.loadMenu("res/Menu.csv");
        this.loadMembers("res/Members.csv");
    }

    /**
     * The main method of the program
     */
    public static void main(String[] args){
        Restaurant restaurant = new Restaurant();
        restaurant.serve();
    }

    /**
     * Method which loads the menu
     * @param filename: Input to this method which is the .csv filename String of the restaurant's menu
     */
    public void loadMenu(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] info = line.split(",");
                String food = info[0];
                String price = info[1];

                menu.put(food, Double.parseDouble(price));

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method which loads the members list
     * @param filename: Input to this method which is of type String of a .csv filename
     */
    public void loadMembers(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] info = line.split(",");
                String num = info[0];
                String name = info[1];
                String freq = info[2];
                String phone = info[3];

                membership.put(num, new Member(num, name,Integer.parseInt(freq), Integer.parseInt(phone)));

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method which displays the menu to the customers
     * @param list: The input to this method which is the menu list itself in the form of a HashMap
     */
    public void displayFood(HashMap<String, Double> list){
        printLine();
        System.out.println("|                       FOOD                       |PRICE |");
        printLine();

        for(String food:list.keySet()) {
            System.out.print("|");
            System.out.print(food);
            for(int i = 0; i < (LENGTH - food.length()); i++){
                System.out.print(" ");
            }

            System.out.print("|");
            String price = df.format(list.get(food));
            int priceLen = price.length();
            switch (priceLen) {
                case (6) -> System.out.print(price + "|");
                case (5) -> System.out.print(price + " |");
                case (4) -> System.out.print(price + "  |");
                default -> System.out.print(price + "   |");
            }
            System.out.println();
        }
        printLine();
    }

    public void printLine(){
        System.out.println("-----------------------------------------------------------");
    }

    /**
     * Method which is the backbone of the program, where the restaurant serves the customers
     * Since they arrive and leave.
     */
    public void serve(){
        boolean running=true;
        System.out.println("WELCOME TO THE RESTAURANT! HERE IS THE MENU!\n");
        this.displayFood(menu);

        System.out.println("NARRATOR: If you are ready to order, just call the waiter (input waiter).");

        while(running) {
            System.out.print("CUSTOMER: ");
            String userIn = scanner.nextLine();

            if (userIn.toLowerCase(Locale.ROOT).contains("waiter")) {
                System.out.println("WAITER: Hi my name is Java and I will be serving you today.");
                this.customerOrder();
                this.serveFood();
                this.callBill();
                this.payBill(discount);
                running=false;
            } else {
                System.out.println("I am sorry I don't understand, but if you are ready just yell for the waiter! " +
                        "(input waiter).");
            }
        }
        this.writeToMembers("res/Members.csv");
        System.out.println("WAITER: Thank you for eating at this restaurant! Hope you will come by again!");
        System.out.println("\n*** END OF SIMULATION ***");
    }

    /**
     * Method which asks the customer what they want to order
     */
    public void customerOrder(){
        boolean validity=false;

        System.out.println("WAITER: What can I get for you?");

        while(!validity){
            System.out.print("CUSTOMER: ");
            String foodItem = scanner.nextLine();

            if(menu.containsKey(foodItem.toUpperCase())){
                boolean running=true;
                double price = menu.get(foodItem.toUpperCase());
                order.put(foodItem, price);

                System.out.println("WAITER: Noted, is that all? (yes/no)");

                while(running) {
                    System.out.print("CUSTOMER: ");
                    String lastOrder = scanner.nextLine();

                    if (lastOrder.toLowerCase(Locale.ROOT).equals("yes")) {
                        System.out.println("WAITER: Thank you, please wait for your order while we prepare it for you.");
                        running=false;
                        validity = true;
                    } else if (lastOrder.toLowerCase(Locale.ROOT).equals("no")) {
                        System.out.println("WAITER: Alright then. What can I get for you?");
                        running=false;
                    } else {
                        System.out.println("WAITER: I am sorry I do not understand. Is that a yes or no?");
                    }
                }
            } else {
                System.out.println("WAITER: Sorry we do not have that in our menu.");
            }
        }
    }

    /**
     * Method which serves the food to the customers the food they have ordered
     */
    public void serveFood(){
        boolean running=true;
        int i=1;
        System.out.println("\nWAITER: Sorry for the wait, here is your food!");

        for(String food:order.keySet()){
            System.out.println("    "+i+". "+food);
            i++;
        }

        while(running){
            System.out.print("TO EAT INPUT \"EAT\": ");
            String makan = scanner.nextLine();
            if(makan.toUpperCase(Locale.ROOT).equals("EAT")){
                System.out.println("CUSTOMER: *eats food* *yum* (IMAGINE YOU ARE EATING GOOD FOOD!).");
                running=false;
            }
        }

    }

    /**
     * Method which allows customers to call for the bill after eating
     */
    public void callBill(){
        boolean running=true;
        System.out.println("\nNARRATOR: If you are done, just call the bill by entering \"bill\". Thank you.");

        while(running){
            System.out.print("CUSTOMER: ");
            String bill = scanner.nextLine();

            if(bill.toLowerCase(Locale.ROOT).equals("bill")){
                System.out.println("WAITER: Here is your bill. I hope you have enjoyed the food!");
                printBill(false);
                running=false;
            } else {
                System.out.println("NARRATOR: Sorry I did not get what you meant. " +
                        "To call the bill, just enter \"bill\". Thank you.");
            }
        }

        running=true;
        while(running){
            System.out.println("WAITER: Do you have a membership with us? (yes/no)");
            System.out.print("CUSTOMER: ");
            String member = scanner.nextLine();

            if(member.toLowerCase(Locale.ROOT).equals("yes")){
                this.membership();
                running=false;
            } else if(member.toLowerCase(Locale.ROOT).equals("no")){
                this.createMembership();
                running=false;
            } else {
                System.out.println("WAITER: I am sorry I did not catch that.");
            }
        }

    }

    /**
     * Method which allows the customers to pay the bill after calling and seeing the bill
     */
    public void payBill(boolean disc){
        boolean running=true;

        System.out.println("NARRATOR: Whenever you are ready to pay, please input \"pay\"");

        while(running){
            System.out.print("CUSTOMER: ");
            String payment = scanner.nextLine();

            if(payment.toLowerCase(Locale.ROOT).equals("pay")){
                boolean invalidPay=true;
                System.out.println("WAITER: Cash or card? (cash/card)");
                while(invalidPay) {
                    System.out.print("CUSTOMER: ");
                    String method = scanner.nextLine();
                    if(method.toLowerCase(Locale.ROOT).equals("cash")){
                        this.cashPayment(disc);
                        invalidPay=false;
                    } else if (method.toLowerCase(Locale.ROOT).equals("card")){
                        this.cardPayment();
                        invalidPay=false;
                    } else {
                        System.out.println("WAITER: Sorry, could you please pay with cash or card? (cash/card)");
                    }
                }
                running=false;
            } else {
                System.out.println("NARRATOR: Just input \"pay\" whenever you are ready.");
            }
        }
    }

    /**
     * Method which enables customers to pay with cash
     */
    public void cashPayment(boolean disc){
        boolean running = true;
        double amountPaid=0;
        double due;
        int[] bill = {1,2,5,10,20,50,100};

        if(disc){
            due=discountedGrandTotal;
        } else {
            due=grandTotal;
        }
        while(running) {
            if(amountPaid>due){
                running=false;
            } else {
                System.out.println("NARRATOR: Enter the notes to pay. (without the dollar sign)");
                System.out.println("NARRATOR: You have paid = "+String.format("%,.2f",amountPaid)+
                        ", remaining = "+String.format("%,.2f",due-amountPaid)+".");
                System.out.println("NOTES: $1, $2, $5, $10, $20, $50, $100.");
                System.out.print("ENTER CASH (per note): ");
                String note = scanner.nextLine();
                for(int i=0; i<note.length(); i++){
                    if(i!=note.length()-1) {
                        if (!Character.isDigit(note.charAt(i))) {
                            System.out.println("WAITER: Please enter a valid cash note.");
                            break;
                        }
                    } else {
                        int cash = Integer.parseInt(note);
                        for(int x=0; x<bill.length;x++){
                            if(x==bill.length-1 && cash!=bill[x]){
                                System.out.println("NARRATOR: Please enter a valid cash note in the provided list.");
                            } else if (cash == bill[x]){
                                amountPaid+=cash;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(amountPaid-due>0) {
            System.out.println("WAITER: You have paid $" + amountPaid+" for $"+String.format("%,.2f",due)+
                    ". Here is a change of $"+String.format("%,.2f",amountPaid - due) + ".");
        } else {
            System.out.println("WAITER: You have paid $" + String.format("%,.2f",amountPaid)+". Thank you.");
        }

    }

    /**
     * Method which enables customers to pay with card
     */
    public void cardPayment(){
        boolean running;
        boolean runningBigLoop = true;
        String card = "";
        String expiryDate = "";

        System.out.println("WAITER: Wait for a sec, I will get the debit machine.");

        while(runningBigLoop) {
            System.out.println("MACHINE: Please enter your card number. (Without spaces)");

            running=true;
            while (running) {
                System.out.print("    CARD NUMBER: ");
                String cardNum = scanner.nextLine();
                if (cardNum.length() == 16) {
                    for (int i = 0; i < cardNum.length(); i++) {
                        if (i != cardNum.length() - 1) {
                            if (!Character.isDigit(cardNum.charAt(i))) {
                                System.out.println("MACHINE: *ERROR* Please enter a valid card number.");
                                break;
                            }
                        } else {
                            card = cardNum;
                            running = false;
                        }
                    }
                } else {
                    System.out.println("MACHINE: *ERROR* Card number must be 16 numbers long. Please re-enter.");
                }
            }

            System.out.println("MACHINE: Please enter your card's expiry (month year; eg. 1221 for December 2021).");
            running = true;
            while (running) {
                System.out.print("    EXPIRY: ");
                String expiry = scanner.nextLine();
                if (expiry.length() == 4) {
                    for (int i = 0; i < expiry.length(); i++) {
                        if (i != expiry.length() - 1) {
                            if (!Character.isDigit(expiry.charAt(i))) {
                                System.out.println("MACHINE: *ERROR* Please enter your card's expiry.");
                                break;
                            }
                        } else {
                            expiryDate = expiry;
                            running = false;
                        }
                    }
                } else {
                    System.out.println("MACHINE: *ERROR* Expiry date must be entered with this format -> 1221 for Dec 2021." +
                            " Please re-enter.");
                }
            }

            System.out.println("MACHINE: Please enter your security key (eg: 123).");
            running = true;
            while (running) {
                System.out.print("    SECURITY KEY: ");
                String secKey = scanner.nextLine();
                if (secKey.length() == 3) {
                    for (int i = 0; i < secKey.length(); i++) {
                        if (i != secKey.length() - 1) {
                            if (!Character.isDigit(secKey.charAt(i))) {
                                System.out.println("MACHINE: *ERROR* Please enter your security key correctly.");
                                break;
                            }
                        } else {
                            running = false;
                        }
                    }
                } else {
                    System.out.println("MACHINE: *ERROR* Security key must be only 3 numbers long. Please re-enter.");
                }
            }

            System.out.print("MACHINE: You are paying with card number \"");
            printCardNum(card);
            System.out.println("\" with expiry date \"" + expiryDate + "\".");
            running = true;
            System.out.println("MACHINE: Do you want to proceed with the payment? (yes/no)");
            while (running) {
                System.out.print("CUSTOMER: ");
                String agree = scanner.nextLine();
                if (agree.toLowerCase(Locale.ROOT).equals("yes")) {
                    running = false;
                    runningBigLoop=false;
                } else if (agree.toLowerCase(Locale.ROOT).equals("no")) {
                    running = false;
                } else {
                    System.out.println(("MACHINE: *ERROR* Please re-enter (yes/no) to proceed with payment."));
                }
            }
        }

        System.out.println("MACHINE: Payment successful.");

    }

    /**
     * Method which prints the card number of a client
     * @param cardNum: Input to this method which is the card number in String format
     */
    public void printCardNum(String cardNum){
        for(int i=0; i<cardNum.length(); i++){
            System.out.print(cardNum.charAt(i));
            if((i+1)%4==0 && (i!=cardNum.length()-1)){
                System.out.print(" ");
            }
        }
    }

    /**
     * Method which asks the customer for their membership
     */
    public void membership(){
        boolean running=true;
        System.out.println("WAITER: Could you please tell me your membership number?");

        while(running){
            System.out.print("CUSTOMER: ");
            String membershipNum = scanner.nextLine();

            if(membership.containsKey(membershipNum)) {
                System.out.println("WAITER: Well Hello " + membership.get(membershipNum).getName() +
                        "! Good to have you with us for " + (membership.get(membershipNum).getFreq() + 1) + " times!");
                System.out.println("WAITER: I have changed your bill with your membership discount.");
                membership.get(membershipNum).setFreq();
                this.discountBill();
                discount=true;
                running = false;
            } else {
                System.out.println("WAITER: I am sorry, I don't think you are in our membership list. Feel free " +
                        "to retry by entering your membership number.");
            }
        }

    }

    /**
     * Method which enables to create a new membership for the customer
     */
    public void createMembership(){
        boolean running=true;
        boolean invalidNum = true;
        System.out.println("WAITER: Would you want a membership with us? (yes/no)");

        while(running){
            System.out.print("CUSTOMER: ");
            String userIn = scanner.nextLine();

            if(userIn.toLowerCase(Locale.ROOT).equals("yes")){
                System.out.println("WAITER: Please enter your name.");
                System.out.print("CUSTOMER: ");
                String name = scanner.nextLine();
                System.out.println("WAITER: Please enter your phone number as well.");

                while(invalidNum) {
                    System.out.print("CUSTOMER: ");
                    String phoneNum = scanner.nextLine();

                    for(int i=0; i<phoneNum.length(); i++){
                        if(i!=phoneNum.length()-1){
                            if(!Character.isDigit(phoneNum.charAt(i))){
                                System.out.println("WAITER: Please enter a valid phone number.");
                                break;
                            }
                        } else {
                            invalidNum=false;

                            boolean validID=false;
                            while(!validID) {
                                int id = random.nextInt(999);
                                String personId = "1166"+id;
                                if(!membership.containsKey(personId)){
                                    System.out.println("WAITER: Thank you for joining our restaurant membership. " +
                                            "Your Membership ID is: "+personId);
                                    membership.put(personId,
                                            new Member(personId, name, 1, Integer.parseInt(phoneNum)));
                                    validID=true;
                                }
                            }

                            System.out.println("WAITER: We have provided a discount for you. This is the new bill.");
                            this.discountBill();
                            running=false;
                            discount=true;
                            break;
                        }
                    }
                }

            } else if(userIn.toLowerCase(Locale.ROOT).equals("no")){
                System.out.println("WAITER: Alright then, no problem.");
                running=false;
            } else {
                System.out.println("WAITER: I am sorry I did not catch that. Would you want a membership with us? " +
                        "(yes/no)");
            }
        }
    }

    /**
     * Method which writes the new members to the .csv file
     * @param filename: Input to this method which is the filename String
     */
    public void writeToMembers(String filename){
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))){
            for(String member:membership.keySet()){
                Member person = membership.get(member);
                pw.println(person.getNum()+","+person.getName()+","+person.getFreq()+","+person.getPhoneNum());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which calculates the discount for the customer and provides an overall discounted bill
     */
    public void discountBill(){
        for(String food:order.keySet()){
            Double price = order.get(food);
            order.replace(food, price*DISCOUNT);
        }
        printBill(true);
    }

    /**
     * Method which prints the bill for the customer
     * @param discounted: Input to this method to know if the bill printed is discounted or not
     */
    public void printBill(boolean discounted){
        printLine();
        System.out.println("|                           BILL                          |");
        printLine();
        System.out.println();

        if(discounted){
            this.displayFood(order);
            for (String food:order.keySet()){
                discountedGrandTotal+=order.get(food);
            }
            System.out.println("TOTAL: $"+String.format("%,.2f", discountedGrandTotal));
            printLine();
        } else {
            this.displayFood(order);
            for (String food:order.keySet()){
                grandTotal+=order.get(food);
            }
            System.out.println("TOTAL: $" + String.format("%,.2f", grandTotal));
            printLine();
        }
    }

}

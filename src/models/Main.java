package models;
import java.util.Scanner;
import java.sql.*;

public class Main {
    public static void main(String[] args) {

        database datab=new database();
        User currentUser; //We will save here the user we will be working in
        Connection conn=datab.db("kto","postgres","0000");

        System.out.println("Welcome to our bank SaBoNa!");
        System.out.println("Thank you for choosing us!");
        Scanner sc = new Scanner(System.in); //creating scanner
        System.out.println("Type 0 to register, type 1 to log in: ");
        while (true) {
            String chose = sc.nextLine();
            if (chose.equals("1")) {
                currentUser = User.login(); //method login returns the user which username and password we have entered
                break;
            } else if (chose.equals("0")) {
                User.register();
                int size = User.getAllUsers().size();
                currentUser = User.getAllUsers().get(size - 1); //We created a new user, saved him in database, saved in arraylist and using it
                break;
            } else {
                System.out.println("Unknown command. Enter 0 to register or enter 1 to log in: ");
            }
        }
        int action;
        while (true){
            System.out.println(
                    "0 - Exit" + '\n' +
                            "1 - Status" + '\n' +
                            "2 - Withdraw money" + '\n' +
                            "3 - Top up your account" + '\n' +
                            "4 - Show all users" + '\n' +
                            "5 - Delete an user"
            );
            action = sc.nextInt();
            Scanner stringg = new Scanner(System.in);

            if (action == 1){ //just gives information about current user
                System.out.println("Username: "+currentUser.getUsername());
                System.out.println("Phone number: "+currentUser.getNumber());
                System.out.println("Your balance: "+currentUser.getBalance()+" kzt");
                System.out.println();
            }
            else if(action == 2){ //simple money transaction between users
                System.out.println("Enter the username of the person you want to transfer: ");
                String tUser = stringg.nextLine();
                System.out.println("Enter the amount of money you want to transfer: ");
                double money = sc.nextDouble();
                if (currentUser.getBalance() - money < 0) {
                    System.out.println("Not enough money on your balance. Try to top up your balance");
                }
                else {
                    User.transferUser(tUser, money, currentUser.getUsername()); //if user doesnt have enough money
                    currentUser.setBalance(currentUser.getBalance()-money);
                    System.out.println("Operation was successful");
                }
            }
            else if(action == 3){ //top up your account with any sum you want :)
                System.out.println("Enter the amount of money you want to top up: ");
                double cash = sc.nextDouble();
                currentUser.setBalance(currentUser.getBalance()+cash);
                User.topUp(currentUser.getUsername(), cash);
            }
            else if(action == 4){ //see all users of the bank (allowded only for admin user)
                if (currentUser instanceof Admin) {
                    Admin adminUser = (Admin) currentUser;
                    System.out.println("All SaBoNa bank users: " + adminUser.getAllUsernames());
                } else {
                    System.out.println("You do not have enough permissions for this!");
                }
            }
            else if(action == 5){ //to delete users from database (allowded only for admin user)
                if (currentUser instanceof Admin) {
                    Admin adminUser = (Admin) currentUser;
                    System.out.println("Enter the username of a user you want to delete: ");
                    String username = stringg.nextLine();
                    adminUser.deleteUser(username); // Call deleteUser method on adminUser
                    System.out.println(username + " was successfully deleted");
                } else {
                    System.out.println("You do not have enough permissions for this!");
                }
            }
            else if(action == 0){break;} //exit
            else{
                System.out.println("Unknown command. Try again");
            }
        }
    }
}
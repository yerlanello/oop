package models;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.SQLException;

public class User {
    private String username;
    private String password;
    private String number;
    private double balance;

    public User(String username, String password, String number, double balance){
        this.username = username;
        this.password = password;
        this.number = number;
        this.balance = balance;
        //constructor for creating an user
    }
    //getters and setters
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    private static ArrayList<User> allUsers = new ArrayList<>();

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public static User register() { //register method
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String username = sc.nextLine();

        System.out.println("Number: ");
        String number = sc.nextLine();

        System.out.println("Create a password: ");
        String password = sc.nextLine();

        System.out.println("Enter the password again: ");
        String passwordCheck = sc.nextLine();

        User newUser;

        if (password.equals(passwordCheck)) {
            if (username.equals("admin") && password.equals("1234")) { //checking is it admin or not
                newUser = new Admin(username, password, number, 1000000);
            } else {
                newUser = new User(username, password, number, 0);
            }
            if (addUserToDb(newUser)) {
                System.out.println("You have been successfully registered");
                allUsers.add(newUser);
            } else {
                System.out.println("Failed to register");
            }
        } else {
            System.out.println("Passwords are not the same. Try again.");
            return register();
        }
        return newUser;
    }
    private static boolean addUserToDb(User user) { //adding user to database after registration
        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "INSERT INTO users (username, password, number, balance) VALUES (?, ?, ?, ?)";
            try (PreparedStatement s = conn.prepareStatement(sql)) {
                s.setString(1, user.getUsername());
                s.setString(2, user.getPassword());
                s.setString(3, user.getNumber());
                s.setDouble(4, user.getBalance());

                int rowsInserted = s.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    public static User login() { //login method
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String username = sc.nextLine();
        System.out.println("Password: ");
        String password = sc.nextLine();

        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "SELECT * FROM users WHERE username = ? and password = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String number = resultSet.getString("number");
                        double balance = resultSet.getDouble("balance");
                        if (username.equals("admin") && password.equals("1234")) { //checks if it is admin
                            return new Admin(username, password, number, balance);
                        } else {
                            return new User(username, password, number, balance);
                        }
                    } else {
                        System.out.println("Failed to login. It seems that your login or password is incorrect. Try again");
                        return User.login();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return new User("", "", "", 0);
        }
    }
    public static void transferUser(String toUser, double receiveMoney, String fromUser) { //method for sending money

        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "UPDATE users SET balance = balance + ? WHERE username = ?;";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setDouble(1, receiveMoney);
                statement.setString(2, toUser);
                statement.executeUpdate();

            }
            sql = "UPDATE users SET balance = balance - ? WHERE username = ?;";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setDouble(1, receiveMoney);
                statement.setString(2, fromUser);
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            System.out.println("User with this username doesn't exist");
        }
    }
    public static void topUp(String username, double topUp) { //money, money to your balance heehheeh
        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "UPDATE users SET balance = balance + ? WHERE username = ?;";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setDouble(1, topUp);
                statement.setString(2, username);

                statement.executeUpdate();
                System.out.println("Balance updated successfully."); }
        } catch (SQLException e) {
            System.out.println("error");
        }
    }
    public ArrayList<String> getAllUsernames() {
        System.out.println("You don't have enough permissions");
        return new ArrayList<>();
    }

    public void deleteUser(String username) {
        System.out.println("You don't have enough permissions");
    }
}

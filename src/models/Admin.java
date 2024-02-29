package models;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
public class Admin extends User {
    public Admin(String username, String password, String number, double balance) {
        super(username, password, number, balance);
    }
    @Override
    public void deleteUser(String usrname) {
        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "DELETE FROM users WHERE username = ?;";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, usrname);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("User with this username doesn't exist");
        }
    }
    @Override
    public ArrayList<String> getAllUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        database db = new database();
        try (Connection conn = db.db("kto", "postgres", "0000")) {
            String sql = "SELECT username FROM users";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("username");
                        usernames.add(username);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return usernames;
    }
}

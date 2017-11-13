package db;

import java.sql.*;

public class DbBackend implements DbContract {

    public static void createTableIfNotExist() {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            String query = "CREATE TABLE IF NOT EXISTS " + CALORIES +
                    "(" + CaloriesTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    CaloriesTable.NAME + "  TEXT    NOT NULL, " +
                    CaloriesTable.CAL + " INT     NOT NULL)";
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropTable() {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            String query = "DROP TABLE IF EXISTS " + CALORIES;
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addProductCaloriesInfo(String name, long cal) {
        String query = "INSERT INTO " + CALORIES + " (" + CaloriesTable.NAME + ", " + CaloriesTable.CAL + ")"
                + " VALUES (\"" + name + "\"," + cal + ")";

        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long getProductCalories(String productName) {
        String query = "SELECT * FROM " + CALORIES + " WHERE " + CaloriesTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            int cal = resultSet.getInt(CaloriesTable.CAL);
            resultSet.close();
            stmt.close();
            return cal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
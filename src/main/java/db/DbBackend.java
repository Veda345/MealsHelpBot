package db;

import java.sql.*;

public class DbBackend implements DbContract {

    public static void createTables() {
        String query = "CREATE TABLE IF NOT EXISTS " + CALORIES +
                "(" + CaloriesTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CaloriesTable.NAME + "     TEXT    UNIQUE  NOT NULL, " +
                CaloriesTable.SERVING + "  TEXT    NOT NULL, " +
                CaloriesTable.CAL + "      INT     NOT NULL)";
        execQuery(query);

        query = "CREATE TABLE IF NOT EXISTS " + PFC +
                "(" + PfcTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                PfcTable.NAME + "     TEXT    UNIQUE  NOT NULL, " +
                PfcTable.SERVING + " TEXT    NOT NULL, " +
                PfcTable.PROTEIN + "  INT     NOT NULL, " +
                PfcTable.CARBS + "    INT     NOT NULL, " +
                PfcTable.FAT + "      INT     NOT NULL)";
        execQuery(query);
    }

    private static void execQuery(String query) {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropTable(String tableName) {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropTables() {
        dropTable(CALORIES);
        dropTable(PFC);
    }


    public static void addProductCaloriesInfo(String name, String serving, long cal) {
        String query = "INSERT INTO " + CALORIES + " (" + CaloriesTable.NAME + ", " + CaloriesTable.SERVING + ", " + CaloriesTable.CAL + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\","  + cal + ")";

        execQuery(query);
    }

    public static void addProductPfcInfo(String name, String serving, long protein, long fat, long carbs) {
        String query = "INSERT INTO " + PFC + " (" + PfcTable.NAME + ", " + PfcTable.SERVING + ", " + PfcTable.PROTEIN
                + ", " + PfcTable.FAT + ", " + PfcTable.CARBS + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\","  + protein  + ", " + fat + ", " + carbs + ")";

        execQuery(query);
    }

    public static ProductInfo getProductCalories(String productName) {
        String query = "SELECT * FROM " + CALORIES + " WHERE " + CaloriesTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            long cal = resultSet.getInt(CaloriesTable.CAL);
            String serving = resultSet.getString(CaloriesTable.SERVING);
            resultSet.close();
            stmt.close();
            return new ProductInfo(productName, serving, cal);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ProductInfo getProductPfc(String productName) {
        String query = "SELECT * FROM " + PFC + " WHERE " + PfcTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            long fat = resultSet.getInt(PfcTable.FAT);
            long protein = resultSet.getInt(PfcTable.PROTEIN);
            long carbs = resultSet.getInt(PfcTable.CARBS);
            String serving = resultSet.getString(PfcTable.SERVING);
            resultSet.close();
            stmt.close();
            return new ProductInfo(productName, serving, protein, fat, carbs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
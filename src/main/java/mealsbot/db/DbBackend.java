package mealsbot.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbBackend implements DbContract {

    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(DbBackend.class);

    static void createTables() {
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

        query = "CREATE TABLE IF NOT EXISTS " + FAVRECIPE +
                "(" + FavRecipeTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FavRecipeTable.PERSONID + " INT NOT NULL, " +
                FavRecipeTable.RECIPEID + " TEXT, " +
                FavRecipeTable.TITLE + "    TEXT, " +
                FavRecipeTable.TIME + "     INTEGER, " +
                FavRecipeTable.ENERGY + "   INTEGER, " +
                FavRecipeTable.IMGURL + "   TEXT, " +
                FavRecipeTable.STEPS  + "   TEXT)";
        execQuery(query);
    }

    private static void execQuery(@NotNull String query) {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            logger.error("Error while executing query {}", query, e);
        }
    }

    private static void dropTable(@NotNull String tableName) {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            logger.error("Error while dropping table {}", tableName, e);
        }
    }

    static void dropTables() {
        dropTable(CALORIES);
        dropTable(PFC);
        dropTable(FAVRECIPE);
    }


    static void addProductCaloriesInfo(@NotNull String name, @NotNull String serving, long cal) {
        String query = "INSERT INTO " + CALORIES + " (" + CaloriesTable.NAME + ", " + CaloriesTable.SERVING + ", " + CaloriesTable.CAL + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\"," + cal + ")";

        execQuery(query);
    }

    static void addProductPfcInfo(@NotNull String name, @NotNull String serving, long protein, long fat, long carbs) {
        String query = "INSERT INTO " + PFC + " (" + PfcTable.NAME + ", " + PfcTable.SERVING + ", " + PfcTable.PROTEIN
                + ", " + PfcTable.FAT + ", " + PfcTable.CARBS + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\"," + protein + ", " + fat + ", " + carbs + ")";

        execQuery(query);
    }
}
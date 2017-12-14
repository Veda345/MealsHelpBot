package db;

import data.ProductInfo;
import data.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DbBackend implements DbContract {

    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(DbBackend.class);

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

    public static void dropTable(@NotNull String tableName) {
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            logger.error("Error while dropping table {}", tableName, e);
        }
    }

    public static void dropTables() {
        dropTable(CALORIES);
        dropTable(PFC);
        dropTable(FAVRECIPE);
    }


    public static void addProductCaloriesInfo(@NotNull String name, @NotNull String serving, long cal) {
        String query = "INSERT INTO " + CALORIES + " (" + CaloriesTable.NAME + ", " + CaloriesTable.SERVING + ", " + CaloriesTable.CAL + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\"," + cal + ")";

        execQuery(query);
    }

    public static void addProductPfcInfo(@NotNull String name, @NotNull String serving, long protein, long fat, long carbs) {
        String query = "INSERT INTO " + PFC + " (" + PfcTable.NAME + ", " + PfcTable.SERVING + ", " + PfcTable.PROTEIN
                + ", " + PfcTable.FAT + ", " + PfcTable.CARBS + ")"
                + " VALUES (\"" + name + "\"," + "\"" + serving + "\"," + protein + ", " + fat + ", " + carbs + ")";

        execQuery(query);
    }

    /**
     * Saves recommended to telegram user recipe
     *
     * @param personId A unique id that identifies telegram user
     * @param recipe   recipe to save
     * @throws SQLException throws if something went wrong while saving
     */
    public static void addFavourite(@NotNull Integer personId, @NotNull Recipe recipe) throws SQLException {
        List<Recipe.Stage> stages = recipe.stages;

        String concatenatedSteps = null;
        if (recipe.stages != null) {
            concatenatedSteps = StringUtils.join(stages.stream().map(stage ->
                    StringUtils.join(stage.steps.stream().filter(s -> !s.equals("")).collect(Collectors.toList()), "@"))
                    .collect(Collectors.toList()), "&");
        }
        String query = "INSERT INTO " + FAVRECIPE + " (" + FavRecipeTable.PERSONID + ", " + FavRecipeTable.RECIPEID + ", "
                + FavRecipeTable.TITLE + ", " + FavRecipeTable.TIME + ", " + FavRecipeTable.ENERGY + ", " + FavRecipeTable.IMGURL
                + ", " + FavRecipeTable.STEPS + ")"
                + "VALUES (" + personId + ", \"" + recipe.id + "\", \"" + recipe.title + "\", " + recipe.time + ", " + recipe.energy + ", \""
                + recipe.imgUrl + "\", \"" + concatenatedSteps + "\")";

        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }
    }

    @NotNull
    public static ProductInfo getProductCalories(@NotNull String productName) {
        String query = "SELECT * FROM " + CALORIES + " WHERE " + CaloriesTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            long cal = resultSet.getInt(CaloriesTable.CAL);
            String serving = resultSet.getString(CaloriesTable.SERVING);
            resultSet.close();
            stmt.close();
            return new ProductInfo.Builder().name(productName).serving(serving).calories(cal).build();
        } catch (SQLException e) {
            logger.error("Error while getting product {} calories, query {}", productName, query, e);
        }
        return null;
    }

    @NotNull
    public static ProductInfo getProductPfc(@NotNull String productName) {
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
            return new ProductInfo.Builder().name(productName).serving(serving).protein(protein).fat(fat).carbs(carbs).build();
        } catch (SQLException e) {
            logger.error("Error while getting product {} pfc, query {}", productName, query, e);
        }
        return null;
    }

    /**
     * Gets all recipes that are saved by telegram user
     *
     * @param personId A unique id that identifies telegram user
     * @return List of saved recommended recipes
     */
    @NotNull
    public static List<Recipe> getFavRecipes(@NotNull Integer personId) {
        String query = "SELECT * FROM " + FAVRECIPE + " WHERE " + FavRecipeTable.PERSONID + "=" + personId;
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Recipe> recipes = new ArrayList<>();
            while (resultSet.next()) {
                String recipeId = resultSet.getString(FavRecipeTable.RECIPEID);
                String title = resultSet.getString(FavRecipeTable.TITLE);
                int time = resultSet.getInt(FavRecipeTable.TIME);
                int energy = resultSet.getInt(FavRecipeTable.ENERGY);
                String imgurl = resultSet.getString(FavRecipeTable.IMGURL);
                String steps = resultSet.getString(FavRecipeTable.STEPS);

                Recipe.Builder builder = new Recipe.Builder()
                        .id(recipeId)
                        .title(title)
                        .time(time)
                        .energy(energy)
                        .imgUrl(imgurl);
                if (steps != null) {
                    List<Recipe.Stage> stages = Arrays.stream(steps.split("&")).map(stage -> {
                        List<String> strings = Arrays.stream(stage.split("@")).filter(s -> !s.equals("")).collect(Collectors.toList());
                        return new Recipe.Stage(strings, imgurl);
                    }).collect(Collectors.toList());
                    builder.stages(stages);
                }
                recipes.add(builder.build());
            }
            return recipes;
        } catch (SQLException e) {
            logger.error("Error while getting person {} favourite recipes, query {}", personId, query, e);
        }
        return null;
    }

    public static boolean clearSavedRecipes(int personId) {
        String query = "DELETE FROM " + FAVRECIPE + " WHERE " + FavRecipeTable.PERSONID + "=" + personId;
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            logger.error("Error while clearing person {} favourite recipes, query {}", personId, query, e);
        }
        return false;
    }
}
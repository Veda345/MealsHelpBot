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

/**
 * Execute requests to database
 */
public class DbAccessor implements DbContract {

    private final static Logger logger = LoggerFactory.getLogger(DbAccessor.class);

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
        String query = "INSERT INTO " + FAVRECIPE + " (" + DbContract.FavRecipeTable.PERSONID + ", " + DbContract.FavRecipeTable.RECIPEID + ", "
                + DbContract.FavRecipeTable.TITLE + ", " + DbContract.FavRecipeTable.TIME + ", " + DbContract.FavRecipeTable.ENERGY + ", " + DbContract.FavRecipeTable.IMGURL
                + ", " + DbContract.FavRecipeTable.STEPS + ")"
                + "VALUES (" + personId + ", \"" + recipe.id + "\", \"" + recipe.title + "\", " + recipe.time + ", " + recipe.energy + ", \""
                + recipe.imgUrl + "\", \"" + concatenatedSteps + "\")";

        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }
    }

    public static ProductInfo getProductCalories(@NotNull String productName) {
        String query = "SELECT * FROM " + CALORIES + " WHERE " + DbContract.CaloriesTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            long cal = resultSet.getInt(DbContract.CaloriesTable.CAL);
            String serving = resultSet.getString(DbContract.CaloriesTable.SERVING);
            resultSet.close();
            stmt.close();
            return new ProductInfo.Builder().name(productName).serving(serving).calories(cal).build();
        } catch (SQLException e) {
            logger.error("Error while getting product {} calories, query {}", productName, query, e);
        }
        return null;
    }

    public static ProductInfo getProductPfc(@NotNull String productName) {
        String query = "SELECT * FROM " + PFC + " WHERE " + DbContract.PfcTable.NAME + "= \"" + productName + "\"";
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            long fat = resultSet.getInt(DbContract.PfcTable.FAT);
            long protein = resultSet.getInt(DbContract.PfcTable.PROTEIN);
            long carbs = resultSet.getInt(DbContract.PfcTable.CARBS);
            String serving = resultSet.getString(DbContract.PfcTable.SERVING);
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
    public static List<Recipe> getFavRecipes(@NotNull Integer personId) {
        String query = "SELECT * FROM " + FAVRECIPE + " WHERE " + DbContract.FavRecipeTable.PERSONID + "=" + personId;
        try (Connection c = DriverManager.getConnection(DATABASE)) {
            Statement stmt = c.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Recipe> recipes = new ArrayList<>();
            while (resultSet.next()) {
                recipes.add(extractRecipe(resultSet));
            }
            return recipes;
        } catch (SQLException e) {
            logger.error("Error while getting person {} favourite recipes, query {}", personId, query, e);
        }
        return null;
    }

    /**
     * Extracts recipe from {@link ResultSet}
     */
    private static Recipe extractRecipe(ResultSet resultSet) throws SQLException {
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
        return builder.build();
    }

    public static boolean clearSavedRecipes(int personId) {
        String query = "DELETE FROM " + FAVRECIPE + " WHERE " + DbContract.FavRecipeTable.PERSONID + "=" + personId;
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

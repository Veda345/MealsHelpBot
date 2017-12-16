package mealsbot.db;

import mealsbot.data.ProductInfo;
import mealsbot.data.Recipe;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;


public class DbAccessorTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Field f = DbContract.class.getDeclaredField("DATABASE");
        f.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.set(null, "test_database");

        DatabaseInitializer.initDb();
    }


    @Test
    public void addSingleFavourite() throws Exception {
        int testId = 1;

        Recipe testRecipe = new Recipe("rId", "rTirle", 10, 10, "url", null);
        DbAccessor.addFavourite(testId, testRecipe);

        List<Recipe> recipes = DbAccessor.getFavRecipes(testId);
        Assert.assertNotNull(recipes);
        Assert.assertEquals(recipes.size(), 1);

        checkRecipesEquality(testRecipe, recipes.get(0));
    }

    private void checkRecipesEquality(Recipe testRecipe, Recipe recipe) {
        Assert.assertEquals(recipe.id, testRecipe.id);
        Assert.assertEquals(recipe.title, testRecipe.title);
        Assert.assertEquals(recipe.time, testRecipe.time);
        Assert.assertEquals(recipe.imgUrl, testRecipe.imgUrl);
        Assert.assertEquals(recipe.energy, testRecipe.energy);
    }


    @Test
    public void addMultipleFavourite() throws Exception {
        int testId = 1;

        DbAccessor.clearSavedRecipes(testId);

        Recipe testRecipe = new Recipe("rId", "rTirle", 10, 10, "url", null);
        DbAccessor.addFavourite(testId, testRecipe);

        Recipe testRecipe2 = new Recipe("rId2", "rTirle2", 10, 10, "url2", null);
        DbAccessor.addFavourite(testId, testRecipe2);

        List<Recipe> recipes = DbAccessor.getFavRecipes(testId);
        Assert.assertNotNull(recipes);
        Assert.assertEquals(recipes.size(), 2);

        checkRecipesEquality(testRecipe, recipes.get(0));
        checkRecipesEquality(testRecipe2, recipes.get(1));
    }

    @Test
    public void getProductCalories() throws Exception {
        ProductInfo milkInfo = DbAccessor.getProductCalories("milk");
        ProductInfo orangesInfo = DbAccessor.getProductCalories("oranges");

        Assert.assertNotNull(milkInfo);
        Assert.assertNotNull(orangesInfo);
        Assert.assertTrue(milkInfo.calories >= 0);
        Assert.assertTrue(orangesInfo.calories >= 0);
        Assert.assertNotEquals(milkInfo.calories, orangesInfo.calories);

        ProductInfo milkInfo2 = DbAccessor.getProductCalories("milk");
        Assert.assertNotNull(milkInfo2);
        Assert.assertEquals(milkInfo.calories, milkInfo2.calories);
    }

    @Test
    public void getProductPfc() throws Exception {
        ProductInfo cheeseInfo = DbAccessor.getProductPfc("lettuce");
        ProductInfo coffeeInfo = DbAccessor.getProductPfc("coffee");

        Assert.assertNotNull(cheeseInfo);
        Assert.assertNotNull(coffeeInfo);

        Assert.assertTrue(cheeseInfo.carbs >= 0);
        Assert.assertTrue(coffeeInfo.carbs >= 0);
        Assert.assertTrue(cheeseInfo.protein >= 0);
        Assert.assertTrue(coffeeInfo.protein >= 0);
        Assert.assertTrue(cheeseInfo.fat >= 0);
        Assert.assertTrue(coffeeInfo.fat >= 0);

        Assert.assertNotEquals(cheeseInfo.carbs, coffeeInfo.carbs);
        Assert.assertNotEquals(cheeseInfo.protein, coffeeInfo.protein);
        Assert.assertEquals(cheeseInfo.fat, coffeeInfo.fat);
    }

    @Test
    public void clearSavedRecipes() throws Exception {
        int testId = 10;

        Recipe testRecipe = new Recipe("rId", "rTirle", 10, 10, "url", null);
        DbAccessor.addFavourite(testId, testRecipe);

        DbAccessor.clearSavedRecipes(testId);
        List<Recipe> recipes = DbAccessor.getFavRecipes(testId);

        Assert.assertNotNull(recipes);
        Assert.assertEquals(recipes.size(), 0);
    }

    @Test
    public void clearOtherSavedRecipes() throws Exception {
        int testId = 2;

        Recipe testRecipe = new Recipe("rId", "rTirle", 10, 10, "url", null);
        DbAccessor.addFavourite(testId, testRecipe);

        DbAccessor.clearSavedRecipes(12);
        List<Recipe> recipes = DbAccessor.getFavRecipes(testId);

        Assert.assertNotNull(recipes);
        Assert.assertEquals(recipes.size(), 1);
        checkRecipesEquality(testRecipe, recipes.get(0));
    }
}
package mealsbot.http;

import com.google.common.collect.Multimap;
import com.sun.istack.internal.NotNull;
import mealsbot.data.Recipe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecipesRequesterTest {

    @Mock
    private JSONParser jsonParser = mock(JSONParser.class);

    @NotNull
    private final RecipesRequester mRequester = new RecipesRequester(jsonParser);

    @Before
    public void setUp() throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("57bfe641a6bae0f91575a084", "курица", 10, 10, "", null));
        recipes.add(new Recipe("57e2411cf36d281f21d84ac1", "Паста с шафраном и рикоттой", 10, 10, "", null));
        when(jsonParser.parseRecipes(anyString())).thenReturn(recipes);
        when(jsonParser.parseRecommendations(anyString())).thenReturn(recipes);
    }

    @Test
    public void test_requestRecommendations() throws Exception {
        Recipe result = mRequester.requestRecommendations();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.id);
        Assert.assertNotNull(result.title);

        String firstId = result.id;
        String firstTitle = result.title;

        result = mRequester.requestRecommendations();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.id);
        Assert.assertNotNull(result.title);

        String secondId = result.id;
        String secondTitle = result.title;

        Assert.assertNotEquals(firstId, secondId);
        Assert.assertNotEquals(firstTitle, secondTitle);
    }

    @Test
    public void requestAllTitleRecipes() throws Exception {
        Multimap<String, String> result = mRequester.requestAllTitleRecipes();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);

        String test = "курица";
        String id = "57bfe641a6bae0f91575a084";
        String wrongId = "57dc0628f36d2873d81b0c93";

        Collection<String> testCollection = result.get(test);

        Assert.assertTrue(testCollection.size() > 0);
        Assert.assertTrue(testCollection.iterator().next().contains(id));
        Assert.assertFalse(testCollection.iterator().next().contains(wrongId));
    }


    @Test
    public void requestFullRecipe() throws Exception {
        String testId = "57e2411cf36d281f21d84ac1";
        String testTitle = "Паста с шафраном и рикоттой";
        Recipe result = mRequester.requestFullRecipe(testId);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.id);
        Assert.assertNotNull(result.title);

        String firstId = result.id;
        String firstTitle = result.title;

        Assert.assertEquals(firstId, testId);
        Assert.assertEquals(firstTitle, testTitle);
    }

}
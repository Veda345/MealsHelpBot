package http;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sun.istack.internal.NotNull;
import data.Recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesRequester {

    private static final String BASE_URL = "https://intense-earth-33481.herokuapp.com/";
    private static final String RECOMMEND_URL = BASE_URL + "recommend/";
    private static final String RECIPES_URL = BASE_URL + "recipes/";

    @NotNull
    private JSONParser jsonParser = new JSONParser();
    @NotNull
    private Map<String, Recipe> allRecipes = new HashMap<>(30);
    @NotNull
    Multimap<String, String> allTitleRecipes = HashMultimap.create();
    @NotNull
    private List<Recipe> cache = new ArrayList<>(3);

    @NotNull
    private String requestUrl(@NotNull String inputUrl) throws IOException {
        URL url = new URL(inputUrl);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
                buffer.append("\n");
            }
            request.disconnect();
            in.close();
        }
        return buffer.toString();
    }

    @NotNull
    public Recipe requestRecommendations() throws IOException, ParseException {
        if (cache.size() == 0) {
            cache.addAll(jsonParser.parseRecommendations(requestUrl(RECOMMEND_URL)));
        }
        if (cache.size() == 0) {
            return null;
        }
        return cache.remove(0);
    }

    @NotNull
    public Multimap<String, String> requestAllTitleRecipes() throws IOException, ParseException {
        if (allTitleRecipes.size() == 0) {
            List<Recipe> recipes = jsonParser.parseRecipes(requestUrl(RECIPES_URL));
            for (Recipe recipe: recipes) {
                String[] titleWords = recipe.title.split("\\s+");
                for (String titleWord: titleWords) {
                    allTitleRecipes.put(titleWord.toLowerCase(),  recipe.id);
                }
            }
        }
        return allTitleRecipes;
    }
    @NotNull
    public Map<String, Recipe> requestAllRecipes() throws IOException, ParseException {
        if (allRecipes.size() == 0) {
            List<Recipe> recipes = jsonParser.parseRecipes(requestUrl(RECIPES_URL));
            for (Recipe recipe: recipes) {
                allRecipes.put(recipe.id, recipe);
            }
        }
        return allRecipes;
    }

    @NotNull
    public Recipe requestFullRecipe(String recipeId) throws IOException, ParseException {
        allRecipes = requestAllRecipes();
        return allRecipes.get(recipeId);
    }

}

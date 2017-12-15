package mealsbot.http;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import mealsbot.data.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

public class RecipesRequester {

    private static final Logger logger = LoggerFactory.getLogger(RecipesRequester.class);

    private static final String BASE_URL = "https://intense-earth-33481.herokuapp.com/";
    private static final String RECOMMEND_URL = BASE_URL + "recommend/";
    private static final String RECIPES_URL = BASE_URL + "recipes/";

    private final JSONParser jsonParser;

    private Map<String, Recipe> allRecipes = new HashMap<>(30);

    private final Multimap<String, String> allTitleRecipes = HashMultimap.create();

    private final List<Recipe> cache = new ArrayList<>(3);

    public RecipesRequester(JSONParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Nullable
    @VisibleForTesting
    public String requestUrl(@NotNull String inputUrl) throws IOException {
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

    @Nullable
    public Recipe requestRecommendations() throws IOException {
        try {
            if (cache.size() == 0) {
                cache.addAll(jsonParser.parseRecommendations(requestUrl(RECOMMEND_URL)));
            }
            if (cache.size() == 0) {
                return null;
            }
            return cache.remove(0);
        } catch (ParseException e) {
            logger.debug("Error during json parse", e);
        }
        return null;
    }

    @Nullable
    public Multimap<String, String> requestAllTitleRecipes() throws IOException {
        try {
            if (allTitleRecipes.size() == 0) {
                List<Recipe> recipes = jsonParser.parseRecipes(requestUrl(RECIPES_URL));
                for (Recipe recipe : recipes) {
                    String[] titleWords = recipe.title.split("\\s+");
                    for (String titleWord : titleWords) {
                        allTitleRecipes.put(titleWord.toLowerCase(), recipe.id);
                    }
                }
            }
            return allTitleRecipes;
        } catch (ParseException e) {
            logger.debug("Error during json parse", e);
        }
        return null;
    }

    @Nullable
    private Map<String, Recipe> requestAllRecipesIfNeeded() throws IOException {
        try {
            if (allRecipes.size() == 0) {
                List<Recipe> recipes = jsonParser.parseRecipes(requestUrl(RECIPES_URL));
                for (Recipe recipe : recipes) {
                    allRecipes.put(recipe.id, recipe);
                }
            }
            return allRecipes;
        } catch (ParseException e) {
            logger.debug("Error during json parse", e);
        }
        return Collections.emptyMap();
    }

    @Nullable
    public Recipe requestFullRecipe(@NotNull String recipeId) throws IOException, ParseException {
        allRecipes = requestAllRecipesIfNeeded();

        if (allRecipes.size() > 0) {
            return allRecipes.get(recipeId);
        }
        return null;
    }
}

package http;

import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class JSONParser {

    @NotNull
    List<Recipe> parseRecommendations(@NotNull String json) throws JSONException, ParseException {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Recipe result = getBasicRecipe(jsonObject);
            recipes.add(result);
        }

        return recipes;
    }

    @NotNull
    List<Recipe> parseRecipes(@NotNull String json) throws JSONException, ParseException {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Recipe result = getBasicRecipe(jsonObject);

            JSONArray stages = jsonObject.getJSONArray("stages");
            List<Recipe.Stage> recipeStages = new ArrayList<>(10);

            for (Object recipeStage : stages) {
                JSONObject stage = (JSONObject) recipeStage;
                String stageImg = stage.getString("image");
                JSONArray stageSteps = stage.getJSONArray("steps");
                List<String> stepTitles = new ArrayList<>(10);
                for (Object stageStep : stageSteps) {
                    JSONObject step = (JSONObject) stageStep;
                    String stepTitle = step.getString("title");
                    stepTitles.add(stepTitle);
                }

                Recipe.Stage resultStage = new Recipe.Stage(stepTitles, stageImg);
                recipeStages.add(resultStage);
            }

            result.stages = recipeStages;
            recipes.add(result);
        }

        return recipes;
    }

    private Recipe getBasicRecipe(JSONObject jsonObject) {
        String id = jsonObject.getString("_id");
        String title = jsonObject.getString("title");
        int time = jsonObject.getInt("time");
        int energy = jsonObject.getInt("energy");
        String imgUrl = jsonObject.getString("image");
        return new Recipe(id, title, time, energy, imgUrl, null);
    }

}

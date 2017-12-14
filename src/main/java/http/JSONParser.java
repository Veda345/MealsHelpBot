package http;

import com.oracle.tools.packager.Log;
import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.*;

class JSONParser {

    @NotNull
    List<Recipe> parseRecommendations(@NotNull String json) throws JSONException, ParseException {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Recipe.Builder result = getBasicRecipe(jsonObject);
            recipes.add(result.build());
        }

        return recipes;
    }

    @NotNull
    List<Recipe> parseRecipes(@NotNull String json) throws JSONException, ParseException {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Recipe.Builder result = getBasicRecipe(jsonObject);

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

            result.stages(recipeStages);
            recipes.add(result.build());
        }

        return recipes;
    }

    @NotNull
    private Recipe.Builder getBasicRecipe(@NotNull JSONObject jsonObject) {
        Recipe.Builder recipeBuilder =  new Recipe.Builder();
        try {
            String id = jsonObject.getString("_id");
            recipeBuilder.id(id);
            String title = jsonObject.getString("title");
            recipeBuilder.title(title);
            int time = jsonObject.getInt("time");
            recipeBuilder.time(time);
            int energy = jsonObject.getInt("energy");
            recipeBuilder.energy(energy);
            String imgUrl = jsonObject.getString("image");
            recipeBuilder.imgUrl(imgUrl);
        } catch (JSONException e) {
            Log.debug("Error during json parse" + jsonObject);
        }
        return recipeBuilder;
    }

}

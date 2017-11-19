package http;

import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class JSONParser {

    @NotNull
    List<Recipe> parse(@NotNull String json) throws JSONException, ParseException {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("_id");
            String title = jsonObject.getString("title");
            int time = jsonObject.getInt("time");
            int energy = jsonObject.getInt("energy");
            String imgUrl = jsonObject.getString("image");
            recipes.add(new Recipe(id, title, time, energy, imgUrl));
        }

        return recipes;
    }

}

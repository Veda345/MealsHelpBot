package http;

import com.sun.istack.internal.NotNull;
import org.json.JSONException;
import sun.jvm.hotspot.utilities.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RecipesRequester {

    private static final String BASE_URL = "https://intense-earth-33481.herokuapp.com/";
    private static final String RECOMMEND_URL = BASE_URL + "recommend/";

    //todo add DI for JSONParser
    private JSONParser jsonParser = new JSONParser();

    private List<Recipe> cache = new ArrayList<>(3);

    //todo find more information about recipe by id

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
            cache.addAll(jsonParser.parse(requestUrl(RECOMMEND_URL)));
        }
        if (cache.size() == 0) {
            return null;
        }
        return cache.remove(0);
    }
}

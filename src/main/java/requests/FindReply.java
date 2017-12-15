package requests;

import com.google.common.collect.Multimap;
import data.Recipe;
import http.RecipesRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.FormattingUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import static requests.RecommendReply.recipeToShortString;

public class FindReply implements Replier  {
    private final static Logger logger = LoggerFactory.getLogger(RecommendReply.class);

    @NotNull
    private RecipesRequester recipesRequester = new RecipesRequester();

    @NotNull
    private ReplyCallback callback;

    private ArrayList<Recipe> allRequestedRecipes;
    private String lastRequest = "";
    private Multimap<String, String> allTitleRecipes = null;
    private Recipe currentRecipe =  null;
    public FindReply(@com.sun.istack.internal.NotNull ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(@NotNull Update update) {

        answer(update, "What recipe do you want to find?");
        lastRequest = "";
        try {
            allTitleRecipes = recipesRequester.requestAllTitleRecipes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answer(@NotNull Update update, @NotNull String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        message.enableHtml(true);
        callback.sendReply(message);
    }

    @Override
    public void reply(@NotNull Update update) {
        String reply = "";
        String request = update.getMessage().getText();
        if (lastRequest.equals("")) {
            lastRequest = "find";
            try {
                allRequestedRecipes = getRequestRecipes(allTitleRecipes, request.toLowerCase());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            if (allRequestedRecipes != null) {
                reply = printShortRecipes(allRequestedRecipes);
            } else {
                reply = RETRY_FIND;
            }
        } else if (lastRequest.equals("find")) {
            lastRequest = "";
            try {
                int num = Integer.parseInt(request) - 1;
                if (num >= 0 && num <= allRequestedRecipes.size()) {
                    try {
                        currentRecipe = allRequestedRecipes.get(num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (currentRecipe != null) {
                        lastRequest = "short";
                        reply = recipeToShortString(currentRecipe);
                    } else {
                        reply = RETRY_MSG;
                    }
                }
            } catch (NumberFormatException nx) {
                reply = RETRY_MSG;
            }

        } else if (lastRequest.equals("short")) {
            lastRequest = "";
            if (request.equals("more")) {
                lastRequest = "more";
                if (currentRecipe != null) {
                    try {
                        currentRecipe = recipesRequester.requestFullRecipe(currentRecipe.id);
                        reply = FormattingUtils.formatTitle(currentRecipe.title) + "\n" +
                                "If you want to se whole recipe, please, insert" +
                                FormattingUtils.formatBoldText("\"next\"\n");
                    } catch (Exception e) {
                        logger.error("Error while requesting and parsing recipe at find", e);
                    }
                } else {
                    logger.warn("Unexpectedly currentRecipe is null at find");
                }
            }
        } else if (lastRequest.equals("more") ) {
            lastRequest = "done";
            if (currentRecipe == null) {
                reply = "Sorry, something goes wrong";
            }

            StringBuilder recipeText = new StringBuilder();
            for (Recipe.Stage stage:currentRecipe.stages) {
                for (String step : stage.steps) {
                    recipeText.append(FormattingUtils.formatPointedText(step));
                }

            }
            reply = recipeText.toString();
        }
        answer(update, reply);
    }

    private String printShortRecipes(Collection<Recipe> allRequestedRecipes) {
        StringBuilder answer = new StringBuilder("We found " + allRequestedRecipes.size());
        if (allRequestedRecipes.size() > 1)
             answer.append("recipes:\n");
        else {
            answer.append("recipe:\n");
        }
        int num = 1;
        for (Recipe recipe: allRequestedRecipes) {
            answer.append(num + ") " + recipe.title + "\n");
        }
        answer.append("Print number of recipe you want to see " +
                "or 0 if you want to try something else");
        return answer.toString();

    }

    private ArrayList<Recipe> getRequestRecipes(Multimap<String, String> allTitleRecipes,
                                                String requestRecipe) throws IOException, ParseException {
        ArrayList<Recipe> allRequestedRecipes = new ArrayList<>();

        Collection<String> recipesId = allTitleRecipes.get(requestRecipe);
        for (String id:recipesId) {
                allRequestedRecipes.add(recipesRequester.requestFullRecipe(id));
        }
        return allRequestedRecipes;
    }

}

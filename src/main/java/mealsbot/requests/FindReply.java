package mealsbot.requests;

import com.google.common.collect.Multimap;
import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import mealsbot.data.Recipe;
import mealsbot.http.RecipesRequester;
import mealsbot.utils.FormattingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static mealsbot.requests.RecommendReply.recipeToShortString;

public class FindReply implements Replier  {

    private final static Logger logger = LoggerFactory.getLogger(RecommendReply.class);

    private final MealsBotCommands replierType = MealsBotCommands.FIND;

    private final RecipesRequester recipesRequester;

    private volatile List<Recipe> allRequestedRecipes;

    private volatile String lastRequest = "";

    private Multimap<String, String> allTitleRecipes;

    private volatile Recipe currentRecipe;

    public FindReply(RecipesRequester recipesRequester) {
        this.recipesRequester = recipesRequester;
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
        ReplyCallback.sendReply(message);
    }

    @Override
    public void reply(@NotNull Update update) {
        String reply = "";
        String request = update.getMessage().getText();
        switch (lastRequest) {
            case "":
                reply = getReplyForEmptyString(request);
                break;
            case "find":
                reply = getReplyForFind(request);
                break;
            case "short":
                reply = getReplyForShort(request);
                break;
            case "more":
                reply = getReplyForMore();
                break;
        }
        answer(update, reply);
    }

    private String getReplyForEmptyString(String request) {
        String reply;
        lastRequest = "find";
        try {
            allRequestedRecipes = getRequestRecipes(allTitleRecipes, request.toLowerCase());
        } catch (IOException | ParseException e) {
            logger.error("Error while getting recipes for request {}", request, e);
        }

        if (allRequestedRecipes != null) {
            reply = printShortRecipes(allRequestedRecipes);
        } else {
            reply = RETRY_FIND;
        }
        return reply;
    }

    private String getReplyForFind(String request) {
        lastRequest = "";
        String reply = "";
        try {
            int num = Integer.parseInt(request) - 1;
            final List<Recipe> allRequestedRecipes = this.allRequestedRecipes;
            if (num >= 0 && num <= allRequestedRecipes.size()) {
                currentRecipe = allRequestedRecipes.get(num);
                if (currentRecipe != null) {
                    lastRequest = "short";
                    reply = recipeToShortString(currentRecipe);
                } else {
                    reply = RETRY_MSG;
                }
            }
        } catch (NumberFormatException nx) {
            logger.error("Can't parse request number {}", request);
            reply = RETRY_MSG;
        }
        return reply;
    }

    private String getReplyForShort(String request) {
        lastRequest = "";
        String reply = "";
        if (request.equals("more")) {
            lastRequest = "more";
            if (currentRecipe != null) {
                try {
                    currentRecipe = recipesRequester.requestFullRecipe(currentRecipe.id);
                    reply = FormattingUtils.formatTitle(currentRecipe.title) + "\n" +
                            "If you want to se whole recipe, please, insert" +
                            FormattingUtils.formatBoldText("\"next\"\n");
                } catch (Exception e) {
                    logger.error("Error while requesting and parsing recipe at find for request {}", request, e);
                    reply = Replier.RETRY_MSG;
                }
            }
            else {
                logger.warn("Unexpectedly currentRecipe is null at find");
                reply = Replier.RETRY_MSG;
            }
        }
        return reply;
    }

    private String getReplyForMore() {
        String reply;
        lastRequest = "done";
        if (currentRecipe == null) {
            reply = "Sorry, something went wrong";
        }
        else {
            StringBuilder recipeText = new StringBuilder();
            for (Recipe.Stage stage : currentRecipe.stages) {
                stage.steps.forEach(step -> recipeText.append(FormattingUtils.formatPointedText(step)));
            }
            reply = recipeText.toString();
        }
        return reply;
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
            answer.append(num).append(") ").append(recipe.title).append("\n");
            num++;
        }

        if (num != 0) {
            answer.append("Print number of recipe you want to see " +
                    "or 0 if you want to try something else");
        }
        return answer.toString();

    }

    private List<Recipe> getRequestRecipes(Multimap<String, String> allTitleRecipes,
                                                String requestRecipe) throws IOException, ParseException {
        List<Recipe> allRequestedRecipes = Collections.synchronizedList(new ArrayList<Recipe>());

        Collection<String> recipesId = allTitleRecipes.get(requestRecipe);
        for (String id : recipesId) {
            allRequestedRecipes.add(recipesRequester.requestFullRecipe(id));
        }
        return allRequestedRecipes;
    }

    @Override
    public MealsBotCommands getReplierType() {
        return replierType;
    }
}

package requests;

import bot.MealsBotCommands;
import bot.ReplyCallback;
import com.sun.istack.internal.Nullable;
import data.Recipe;
import http.RecipesRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.SingletonsCreator;
import utils.FormattingUtils;
import data.RecommendCache;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.text.ParseException;

public class RecommendReply implements Replier {

    private final static Logger logger = LoggerFactory.getLogger(RecommendReply.class);

    private MealsBotCommands replierType = MealsBotCommands.RECOMMEND;

    private static final String REQUEST_MORE = "more";
    private static final String REQUEST_NEXT = "next";

    @NotNull
    private RecipesRequester recipesRequester = new RecipesRequester();
    @NotNull
    private RecommendCache recommendCache = SingletonsCreator.recommendCache();
    @Nullable
    private Recipe currentRecipe = null;
    @Nullable
    private State currentState = null;

    @Override
    public void initCall(@NotNull Update update) {
        String reply;
        Recipe recipe = null;
        try {
            recipe = recipesRequester.requestRecommendations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recipe != null) {
            reply = recipeToShortString(recipe);
            try {
                recommendCache.addRecommended(update.getMessage().getFrom().getId(), recipesRequester.requestFullRecipe(recipe.id));
            } catch (IOException | ParseException e) {
                logger.error("Error while requesting and parsing full recipe", e);
            }
        } else {
            reply = RETRY_MSG;
        }
        currentRecipe = recipe;
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        message.enableHtml(true);
        ReplyCallback.sendReply(message);

        message.setText("If you like your last recommended meal you can use \'/addtofav\' to save it to your favourite," +
                " or type \"more\" for getting cooking steps");
        ReplyCallback.sendReply(message);
    }

    @Override
    public void reply(@NotNull Update update) {
        String request = update.getMessage().getText();

        if (request.contains(REQUEST_MORE)) {
            if (currentRecipe != null) {
                try {
                    currentRecipe = recipesRequester.requestFullRecipe(currentRecipe.id);
                    currentState = new InitState();
                } catch (Exception e) {
                    currentState = new ErrorState();
                    logger.error("Error while requesting and parsing recipe", e);
                }
            } else {
                currentState = new ErrorState();
                logger.warn("Unexpectedly currentRecipe is null");
            }

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(currentState.getReply());
            message.enableHtml(true);
            ReplyCallback.sendReply(message);

        } else if (request.contains(REQUEST_NEXT)) {
            if (currentRecipe == null) {
                currentState = new ErrorState();
            }
            String reply = currentState.getReply();
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(reply.equals("\n") || reply.equals("") ? "Try typing \"next\" again" : reply);
            message.enableHtml(true);
            ReplyCallback.sendReply(message);
        } else {
            initCall(update);
        }
    }

    static String recipeToShortString(@NotNull Recipe recipe) {

        return "What about \"" + FormattingUtils.formatTitle(recipe.title) + "\"?\n" +
                FormattingUtils.formatBoldText("Time for cooking: ") + recipe.time + " min\n" +
                FormattingUtils.formatBoldText("Energy: ") + recipe.energy + " Kcal\n" +
                recipe.imgUrl + "\n";
    }

    private abstract class State {
        @NotNull
        abstract String getReply();
    }

    private class InitState extends State {

        @NotNull
        @Override
        String getReply() {
            String result = FormattingUtils.formatTitle(currentRecipe.title) + "\n"+
                    "Чтобы увидеть рецепт по шагам, введите "+
                    FormattingUtils.formatBoldText("\"next\"\n");
            currentState = new StepState(0);
            return result;
        }
    }

    private class StepState extends State {
        private int stepNum = 0;

        StepState(int num) {
            stepNum = num;
        }

        @NotNull
        @Override
        String getReply() {
            if (stepNum >= currentRecipe.stages.size()) {
                return FormattingUtils.formatBoldText("No more steps!");
            }

            Recipe.Stage stage = currentRecipe.stages.get(stepNum);
            StringBuilder result = new StringBuilder();
            for (String str: stage.steps) {
                if (!str.equals("")) {
                    result.append(FormattingUtils.formatPointedText(str));
                }
            }
            result.append(stage.imgUrl).append("\n");

            currentState = new StepState(stepNum + 1);
            return result.toString();
        }
    }

    private class ErrorState extends State {

        @NotNull
        private static final String REPLY_ERROR = "Internal error occurred! Try another command...";

        @NotNull
        @Override
        String getReply() {
            return FormattingUtils.formatBoldText(REPLY_ERROR);
        }
    }

    @Override
    public MealsBotCommands getReplierType() {
        return replierType;
    }
}

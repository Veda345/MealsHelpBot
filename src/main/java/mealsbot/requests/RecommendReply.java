package mealsbot.requests;

import com.sun.istack.internal.Nullable;
import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import mealsbot.data.Recipe;
import mealsbot.data.RecommendCache;
import mealsbot.http.RecipesRequester;
import mealsbot.utils.FormattingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;

public class RecommendReply implements Replier {

    private final static Logger logger = LoggerFactory.getLogger(RecommendReply.class);

    private final MealsBotCommands replierType = MealsBotCommands.RECOMMEND;

    private static final String REQUEST_MORE = "more";

    private static final String REQUEST_NEXT = "next";

    private final RecipesRequester recipesRequester;

    private final RecommendCache recommendCache;

    @Nullable
    private volatile Recipe currentRecipe;

    @Nullable
    private volatile State currentState;

    @Nullable
    private ReplyCallback replyCallback = null;

    public RecommendReply(RecipesRequester recipesRequester, RecommendCache recommendCache) {
        this.recipesRequester = recipesRequester;
        this.recommendCache = recommendCache;
    }

    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        String reply;
        Recipe recipe = null;

        try {
            recipe = recipesRequester.requestRecommendations();
        } catch (Exception e) {
            logger.error("Error while requesting recipes", e);
        }

        if (recipe != null) {
            reply = "What about \"" + recipeToShortString(recipe);
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
        replyCallback.sendReply(message);

        message.setText("If you like your last recommended meal you can use \'/addtofav\' to save it to your favourite," +
                " or type \"more\" for getting cooking steps");
        replyCallback.sendReply(message);
    }

    @Override
    public void reply(@NotNull Update update) {
        String request = update.getMessage().getText();

        if (request.contains(REQUEST_MORE)) {
            sendReplyForMore(update);
        } else if (request.contains(REQUEST_NEXT)) {
            sendReplyForNext(update);
        } else {
            initCall(update);
        }
    }

    private void sendReplyForMore(@NotNull Update update) {
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

        SendMessage message = createSendMessage(update, currentState.getReply());
        replyCallback.sendReply(message);
    }

    @NotNull
    private SendMessage createSendMessage(@NotNull Update update, String text) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(text);
        message.enableHtml(true);
        return message;
    }

    @NotNull
    private void sendReplyForNext(@NotNull Update update) {
        if (currentRecipe == null) {
            currentState = new ErrorState();
        }
        String reply = currentState.getReply();
        SendMessage message = createSendMessage(update,
                reply.equals("\n") || reply.equals("") ? "Try typing \"next\" again" : reply);
        replyCallback.sendReply(message);
    }

    @NotNull
    static String recipeToShortString(@NotNull Recipe recipe) {
        return  FormattingUtils.formatTitle(recipe.title) + "\"?\n" +
                FormattingUtils.formatBoldText("Time for cooking: ") + recipe.time + " min\n" +
                FormattingUtils.formatBoldText("Energy: ") + recipe.energy + " Kcal\n" +
                recipe.imgUrl + "\n";
    }

    private abstract class State {
        @NotNull
        abstract String getReply();
    }

    private class InitState extends State {
        @Override
        String getReply() {
            String result = FormattingUtils.formatTitle(currentRecipe.title) + "\n"+
                    "If you want to see whole recipe, insert "+
                    FormattingUtils.formatBoldText("\"more\"\n");
            currentState = new StepState(0);
            return result;
        }
    }

    private class StepState extends State {
        private int stepNum = 0;

        StepState(int num) {
            stepNum = num;
        }

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
        private static final String REPLY_ERROR = "Internal error occurred! Try another command...";

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

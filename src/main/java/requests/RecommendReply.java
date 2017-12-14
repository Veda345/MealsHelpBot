package requests;

import com.sun.istack.internal.Nullable;
import http.Recipe;
import http.RecipesRequester;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.BeanCreator;
import utils.FormattingUtils;
import utils.RecommendCache;

import javax.validation.constraints.NotNull;

public class RecommendReply implements Replier {

    private static final String REQUEST_MORE = "more";
    private static final String REQUEST_NEXT = "next";

    @NotNull
    private RecipesRequester recipesRequester = new RecipesRequester();
    @NotNull
    private ReplyCallback callback;
    @NotNull
    private RecommendCache recommendCache = BeanCreator.recommendCache();
    @Nullable
    private Recipe currentRecipe = null;
    @Nullable
    private State currentState = null;

    public RecommendReply(@NotNull ReplyCallback callback) {
        this.callback = callback;
    }

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
            recommendCache.addRecommended(update.getMessage().getFrom().getId(), recipe);
        } else {
            reply = RETRY_MSG;
        }
        currentRecipe = recipe;
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        message.enableHtml(true);
        callback.sendReply(message);

        message.setText("If you like your last recommended meal you can use \'/addtofav\' to save it to your favourite");
        callback.sendReply(message);
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
                    currentState = new  ErrorState();
                    e.printStackTrace();
                }
            } else {
                currentState = new  ErrorState();
            }

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(currentState.getReply());
            message.enableHtml(true);
            callback.sendReply(message);

        } else if (request.contains(REQUEST_NEXT)) {
            if (currentRecipe == null) {
                currentState = new  ErrorState();
            }
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(currentState.getReply());
            message.enableHtml(true);
            callback.sendReply(message);
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
                result.append(FormattingUtils.formatPointedText(str));
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
}

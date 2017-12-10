package requests;

import http.Recipe;
import http.RecipesRequester;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.BeanCreator;
import utils.RecommendCache;

public class RecommendReply implements Replier {

    private static final String REQUEST_MORE = "more";
    private static final String REQUEST_NEXT = "next";

    //todo DI
    private RecipesRequester recipesRequester = new RecipesRequester();
    //todo DI
    private ReplyCallback callback;
    //todo DI
    private RecommendCache recommendCache = BeanCreator.recommendCache();

    private Recipe currentRecipe = null;
    private State currentState = null;

    public RecommendReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        String reply;
        Recipe recipe = null;
        try {
            recipe = recipesRequester.requestRecommendations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recipe != null) {
            reply = recipeToShortString(recipe) +
                    "\nIf you like your last recommended meal you can use \'/addtofav\' to save it to your favourite";
            recommendCache.addRecommended(update.getMessage().getFrom().getId(), recipe);
        } else {
            reply = RETRY_MSG;
        }
        currentRecipe = recipe;

        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }

    @Override
    public void reply(Update update) {
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
            callback.sendReply(message);

        } else if (request.contains(REQUEST_NEXT)) {
            if (currentRecipe == null) {
                currentState = new  ErrorState();
            }
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(currentState.getReply());
            callback.sendReply(message);

        } else {
            initCall(update);
        }
    }

    static String recipeToShortString(Recipe recipe) {
        return "What about \"" + recipe.title + "\"?\nTime for cooking: " + recipe.time + " min\nEnergy: " +
                recipe.energy + " Kcal\n" + recipe.imgUrl;
    }

    private abstract class State {
        abstract String getReply();
    }

    private class InitState extends State {

        @Override
        String getReply() {
            String result = currentRecipe.title + "\nЧтобы увидеть рецепт по шагам, введите \"next\"\n";
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
                return "No more steps!";
            }

            Recipe.Stage stage = currentRecipe.stages.get(stepNum);
            StringBuilder result = new StringBuilder();
            for (String str: stage.steps) {
                result.append(str).append("\n");
            }
            result.append(stage.imgUrl);

            currentState = new StepState(stepNum + 1);
            return result.toString();
        }


    }

    private class ErrorState extends State {

        private static final String REPLY_ERROR = "Internal error occurred! Try another command...";

        @Override
        String getReply() {
            return REPLY_ERROR;
        }
    }
}

package requests;

import http.Recipe;
import http.RecipesRequester;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.BeanCreator;
import utils.RecommendCache;

public class RecommendReply implements Replier {

    //todo DI
    private RecipesRequester recipesRequester = new RecipesRequester();
    //todo DI
    private ReplyCallback callback;
    //todo DI
    private RecommendCache recommendCache = BeanCreator.recommendCache();

    public RecommendReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        reply(update);
    }

    @Override
    public void reply(Update update) {
        String reply;
        Recipe recipe = null;
        try {
            recipe = recipesRequester.requestRecommendations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recipe != null) {
            reply = recipeToString(recipe) +
                    "\nIf you like your last recommended meal you can use \'/addtofav\' to save it to your favourite";
            recommendCache.addRecommended(update.getMessage().getFrom().getId(), recipe);
        } else {
            reply = RETRY_MSG;
        }

        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }

    static String recipeToString(Recipe recipe) {
        return "What about \"" + recipe.title + "\"?\nTime for cooking: " + recipe.time + " min\nEnergy: " +
                recipe.energy + " Kcal\n" + recipe.imgUrl;
    }
}

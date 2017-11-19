package requests;

import http.Recipe;
import http.RecipesRequester;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class RecommendReply implements Replier {

    //todo DI
    private RecipesRequester recipesRequester = new RecipesRequester();
    //todo DI
    private ReplyCallback callback;

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
            reply = "What about \"" + recipe.title + "\"?\nTime for cooking: " + recipe.time + " min\nEnergy: " +
                    recipe.energy + " Kcal\n" + recipe.imgUrl;
        } else {
            reply = RETRY_MSG;
        }

        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }
}

package requests;

import db.DbBackend;
import data.Recipe;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Gets all saved user's recommendation
 */
public class FavReply implements Replier {

    @NotNull
    private ReplyCallback callback;

    public FavReply(@NotNull ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        Integer personId = update.getMessage().getFrom().getId();
        List<Recipe> favRecipes = DbBackend.getFavRecipes(personId);

        StringBuilder reply = new StringBuilder();
        if (favRecipes == null) {
            reply.append(RETRY_MSG);
        }
        else {
            if (favRecipes.isEmpty()) {
                reply.append("You have no favourite recipes");
            }
            else {
                favRecipes.forEach(recipe -> reply.append(RecommendReply.recipeToShortString(recipe)).append("\n\n"));
            }
        }
        answer(update, reply.toString());
    }

    private void answer(@NotNull Update update, @NotNull String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        message.enableHtml(true);
        callback.sendReply(message);
    }
}

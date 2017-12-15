package mealsbot.requests;

import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import com.sun.istack.internal.NotNull;
import mealsbot.data.RecommendCache;
import mealsbot.db.DbAccessor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import mealsbot.utils.SingletonsCreator;

public class ClearReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.CLEAR;

    @NotNull
    private RecommendCache recommendCache = SingletonsCreator.recommendCache();

    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        Integer personId = update.getMessage().getFrom().getId();
        if (DbAccessor.clearSavedRecipes(personId)) {
            recommendCache.deleteRecommended(personId);
            answer(update, "All favorites cleared!");
        }
        else {
            answer(update, RETRY_MSG);
        }
    }

    private void answer(@NotNull Update update, @NotNull String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        ReplyCallback.sendReply(message);
    }

    @Override
    public MealsBotCommands getReplierType() {
        return replierType;
    }
}

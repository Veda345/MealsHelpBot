package mealsbot.requests;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import mealsbot.data.Recipe;
import mealsbot.data.RecommendCache;
import mealsbot.db.DbAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.SQLException;

/**
 * Saves (last) user's recommendation that is contained in {@link RecommendCache}
 */
public class AddToFavReply implements Replier {

    private final static Logger logger = LoggerFactory.getLogger(AddToFavReply.class);

    private final RecommendCache recommendCache;

    private final MealsBotCommands replierType = MealsBotCommands.ADDTOFAV;

    @Nullable
    private ReplyCallback replyCallback;

    /**
     * String meaning that user doesn't get any recommendation after reboot
     */
    private final static String UNKNOWN_RECOMMENDATION_MSG = "You don't have any recent recommendation";

    public AddToFavReply(RecommendCache recommendCache) {
        this.recommendCache = recommendCache;
    }

    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        Integer personId = update.getMessage().getFrom().getId();

        Recipe recommended = recommendCache.getRecommended(personId);
        String reply;
        if (recommended == null) {
            reply = UNKNOWN_RECOMMENDATION_MSG;
        }
        else {
            try {
                DbAccessor.addFavourite(personId, recommended);
                recommendCache.deleteRecommended(personId);
                reply = "Successfully saved";
            }
            catch (SQLException e) {
                reply = RETRY_MSG;
                logger.error("Error while saving recipe {} to person {}", recommended, personId, e);
            }
        }
        answer(update, reply);
    }

    private void answer(@NotNull Update update, @NotNull String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        replyCallback.sendReply(message);
    }

    @Override
    public MealsBotCommands getReplierType() {
        return replierType;
    }
}

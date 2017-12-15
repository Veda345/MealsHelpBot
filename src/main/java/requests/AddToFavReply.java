package requests;

import com.sun.istack.internal.NotNull;
import db.DbBackend;
import data.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.SingletonsCreator;
import data.RecommendCache;

import java.sql.SQLException;

/**
 * Saves (last) user's recommendation that is contained in {@link RecommendCache}
 */
public class AddToFavReply implements Replier {

    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(AddToFavReply.class);
    @NotNull
    private RecommendCache recommendCache = SingletonsCreator.recommendCache();
    @NotNull
    private ReplyCallback callback;

    /**
     * String meaning that user doesn't get any recommendation after reboot
     */
    @NotNull
    private static String UNKNOWN_RECOMMENDATION_MSG = "You don't have any recent recommendation";


    public AddToFavReply(@NotNull ReplyCallback callback) {
        this.callback = callback;
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
                DbBackend.addFavourite(personId, recommended);
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
        callback.sendReply(message);
    }

}

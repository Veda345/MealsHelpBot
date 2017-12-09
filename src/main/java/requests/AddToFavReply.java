package requests;

import db.DbBackend;
import http.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.BeanCreator;
import utils.RecommendCache;

import java.sql.SQLException;

public class AddToFavReply implements Replier {

    private final static Logger logger = LoggerFactory.getLogger(AddToFavReply.class);

    //todo DI
    private RecommendCache recommendCache = BeanCreator.recommendCache();

    //todo DI
    private ReplyCallback callback;

    private static String UNKNOWN_RECOMMENDATION_MSG = "You don't have any recent recommendation";


    public AddToFavReply(ReplyCallback callback) {
        this.callback = callback;
    }


    @Override
    public void initCall(Update update) {
        reply(update);
    }

    @Override
    public void reply(Update update) {
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

    private void answer(Update update, String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }

}

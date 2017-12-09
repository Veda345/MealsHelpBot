package requests;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import utils.BeanCreator;
import utils.RecommendCache;

public class ClearReply implements Replier {

    //todo DI
    private ReplyCallback callback;
    private RecommendCache recommendCache = BeanCreator.recommendCache();

    public ClearReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        reply(update);
    }

    @Override
    public void reply(Update update) {
        Integer personId = update.getMessage().getFrom().getId();
        recommendCache.deleteRecommended(personId);
        answer(update, "All favorites cleared!");
    }

    private void answer(Update update, String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }
}

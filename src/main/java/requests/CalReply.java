package requests;

import db.DbBackend;
import db.ProductInfo;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class CalReply implements Replier {

    //todo DI
    private ReplyCallback callback;

    public CalReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        answer(update, "The caloric value of which product do you want to know?");
    }

    @Override
    public void reply(Update update) {
        String reply;

        ProductInfo info = DbBackend.getProductCalories(update.getMessage().getText());
        if (info != null) {
            reply =  info.calories + " cal per serving (" + info.serving + ")";
        } else {
            reply = DONT_KNOW_MSG;
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

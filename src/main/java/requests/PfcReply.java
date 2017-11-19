package requests;

import db.DbBackend;
import db.ProductInfo;
import http.Recipe;
import http.RecipesRequester;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class PfcReply implements Replier {

    //todo DI
    private ReplyCallback callback;

    public PfcReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        answer(update, "The protein, fat and carb values of which product do you want to know?");
    }

    @Override
    public void reply(Update update) {
        String reply;

        ProductInfo info = DbBackend.getProductPfc(update.getMessage().getText());
        if (info != null) {
            reply = "Protein " + info.protein + "g | Fat " + info.fat + "g | Carbs " + info.carbs + "g | per serving (" + info.serving + ")";
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

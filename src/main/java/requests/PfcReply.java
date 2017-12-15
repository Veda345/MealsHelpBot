package requests;

import db.DbBackend;
import data.ProductInfo;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;

public class PfcReply implements Replier {

    @NotNull
    private ReplyCallback callback;

    public PfcReply(@NotNull ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        answer(update, "The protein, fat and carb values of which product do you want to know?");
    }

    @Override
    public void reply(@NotNull Update update) {
        String reply;

        ProductInfo info = DbBackend.getProductPfc(update.getMessage().getText());
        if (info != null) {
            reply = "Protein " + info.protein + "g | Fat " + info.fat + "g | Carbs " + info.carbs + "g | per serving (" + info.serving + ")";
        } else {
            reply = DONT_KNOW_MSG;
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

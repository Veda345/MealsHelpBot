package requests;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class NoOpReply implements Replier {
    //todo DI
    private ReplyCallback callback;

    public NoOpReply(ReplyCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initCall(Update update) {
        reply(update);
    }

    @Override
    public void reply(Update update) {
        answer(update, "Try another command!");
    }

    private void answer(Update update, String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }
}

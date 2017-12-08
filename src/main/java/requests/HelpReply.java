package requests;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class HelpReply implements Replier {

    //todo DI
    private ReplyCallback callback;

    public HelpReply(ReplyCallback callback) {
        this.callback = callback;
    }


    @Override
    public void initCall(Update update) {
        reply(update);
    }

    @Override
    public void reply(Update update) {
        answer(update, "Please type \'/cal\' for getting calories for product info,\n" +
                "\'/pfc\' for getting protein, fat and carb for product info,\n" +
                "\'/recommend\' for getting an advice,\n" +
                "\'/fav\' for getting your favourite recipes,\n" +
                "\'/addtofav\' to save your recent recommendation");
    }

    private void answer(Update update, String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        callback.sendReply(message);
    }
}

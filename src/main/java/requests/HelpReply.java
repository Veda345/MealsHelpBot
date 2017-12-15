package requests;

import bot.MealsBotCommands;
import bot.ReplyCallback;
import com.sun.istack.internal.NotNull;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class HelpReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.HELP;


    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        answer(update, "Please type \'/cal\' for getting calories for product info,\n" +
                "\'/pfc\' for getting protein, fat and carb for product info,\n" +
                "\'/recommend\' for getting an advice,\n" +
                "\'/fav\' for getting your favourite recipes,\n" +
                "\'/addtofav\' to save your recent recommendation");
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

package mealsbot.requests;

import com.sun.istack.internal.Nullable;
import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import com.sun.istack.internal.NotNull;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class HelpReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.HELP;

    @Nullable
    private ReplyCallback replyCallback = null;

    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

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
                "\'/addtofav\' to save your recent recommendation,\n" +
                "\'/find\' to find recipes with some words in title");
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

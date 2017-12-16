package mealsbot.requests;

import com.sun.istack.internal.Nullable;
import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import com.sun.istack.internal.NotNull;
import mealsbot.db.DbAccessor;
import mealsbot.data.ProductInfo;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

public class CalReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.CAL;

    @Nullable
    private ReplyCallback replyCallback = null;

    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        answer(update, "The caloric value of which product do you want to know?");
    }

    @Override
    public void reply(@NotNull Update update) {
        String reply;

        ProductInfo info = DbAccessor.getProductCalories(update.getMessage().getText());
        if (info != null) {
            reply =  info.calories + " cal per serving (" + info.serving + ")";
        } else {
            reply = DONT_KNOW_MSG;
        }

        answer(update, reply);
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

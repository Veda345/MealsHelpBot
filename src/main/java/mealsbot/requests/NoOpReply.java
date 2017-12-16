package mealsbot.requests;

import mealsbot.bot.MealsBotCommands;
import mealsbot.bot.ReplyCallback;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;

public class NoOpReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.NONE;

    private final ReplyCallback replyCallback;

    private static final String tryAnother = "Try another command!";

    public NoOpReply(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        answer(update, tryAnother);
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

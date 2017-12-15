package requests;

import bot.MealsBotCommands;
import bot.MealsHelpBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;

public class NoOpReply implements Replier {

    private final MealsBotCommands replierType = MealsBotCommands.NONE;

    @Override
    public void initCall(@NotNull Update update) {
        reply(update);
    }

    @Override
    public void reply(@NotNull Update update) {
        answer(update, "Try another command!");
    }

    private void answer(@NotNull Update update, String reply) {
        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(reply);
        MealsHelpBot.sendReply(message);
    }

    @Override
    public MealsBotCommands getReplierType() {
        return replierType;
    }
}

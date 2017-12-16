package mealsbot.bot;

import mealsbot.requests.MealsReplyKeyboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.validation.constraints.NotNull;

@Component
public class ReplyCallback {

    private static Logger logger = LoggerFactory.getLogger(ReplyCallback.class);

    private final static MealsReplyKeyboard replyKeyboard = new MealsReplyKeyboard();

    @NotNull
    private static MealsHelpBot mealsHelpBot;

    @Autowired
    public ReplyCallback(MealsHelpBot mealsHelpBot) {
        ReplyCallback.mealsHelpBot = mealsHelpBot;
    }

    public static void sendReply(@NotNull SendMessage message) {
        try {
            message.setReplyMarkup(replyKeyboard.getKeyboardMarkup());
            mealsHelpBot.sendMessage(message);
        } catch (TelegramApiException e) {
            logger.error("Error while sending a message", e);
        }
    }
}

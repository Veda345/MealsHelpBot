package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import requests.MealsReplyKeyboard;
import utils.SingletonsCreator;

import javax.validation.constraints.NotNull;

public interface ReplyCallback {

    Logger logger = LoggerFactory.getLogger(ReplyCallback.class);

    MealsReplyKeyboard replyKeyboard = new MealsReplyKeyboard();

    static void sendReply(@NotNull SendMessage message) {
        try {
            message.setReplyMarkup(replyKeyboard.getKeyboardMarkup());
            SingletonsCreator.mealsHelpBot().sendMessage(message);
        } catch (TelegramApiException e) {
            logger.error("Error while sending a message", e);
        }
    }
}

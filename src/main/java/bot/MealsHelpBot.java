package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import requests.*;
import utils.SingletonsCreator;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MealsHelpBot extends TelegramLongPollingBot {

    @NotNull
    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    private static final Logger logger = LoggerFactory.getLogger(MealsHelpBot.class);

    private static final MealsReplyKeyboard replyKeyboard = new MealsReplyKeyboard();

    @NotNull
    private Replier currentReplier = command2Replier.get(MealsBotCommands.NONE);

    @NotNull
    private static Map<MealsBotCommands, Replier> command2Replier = new HashMap<>();

    public MealsHelpBot() {
        List<Replier> repliers = SingletonsCreator.getRepliers();
        command2Replier.putAll(repliers.stream().collect(Collectors.toMap(Replier::getReplierType, Function.identity())));
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // if update.getMessage().hasText() then text != null
            String query = update.getMessage().getText();

            query = query.toLowerCase();

            if (query.startsWith("/")) {
                MealsBotCommands command = getNewCommand(query);
                currentReplier = command2Replier.get(command);
                if (currentReplier != null) {
                    currentReplier.initCall(update);
                }
            } else {
                currentReplier.reply(update);
            }
        }
    }

    public static void sendReply(@NotNull SendMessage message) {
        try {
            message.setReplyMarkup(replyKeyboard.getKeyboardMarkup());
            SingletonsCreator.mealsHelpBot().sendMessage(message);
        } catch (TelegramApiException e) {
            logger.error("Error while sending a message", e);
        }
    }

    /**
     * If no command fits query string, returns {@link MealsBotCommands#NONE}
     */
    @NotNull
    private MealsBotCommands getNewCommand(@NotNull String query) {
        return MealsBotCommands.getCommandByName(query);
    }

    @Override
    public String getBotUsername() {
        return "MealsHelpBot";
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }
}

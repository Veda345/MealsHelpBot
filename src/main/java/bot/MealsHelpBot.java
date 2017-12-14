package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import requests.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class MealsHelpBot extends TelegramLongPollingBot implements ReplyCallback {

    @NotNull
    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    @NotNull
    private static Map<MealsBotCommands, Replier> command2Replier = new HashMap<>();

    {
        command2Replier.put(MealsBotCommands.CAL, new CalReply(this));
        command2Replier.put(MealsBotCommands.PFC, new PfcReply(this));
        command2Replier.put(MealsBotCommands.RECOMMEND, new RecommendReply(this));
        command2Replier.put(MealsBotCommands.NONE, new NoOpReply(this));
        command2Replier.put(MealsBotCommands.HELP, new HelpReply(this));
        command2Replier.put(MealsBotCommands.ADDTOFAV, new AddToFavReply(this));
        command2Replier.put(MealsBotCommands.FAV, new FavReply(this));
        command2Replier.put(MealsBotCommands.CLEAR, new ClearReply(this));
    }

    @NotNull
    private Replier currentReplier = command2Replier.get(MealsBotCommands.NONE);
    @NotNull
    private MealsReplyKeyboard replyKeyboard = new MealsReplyKeyboard();

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String query = update.getMessage().getText();
            if (query == null) {
                return;
            }

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

    private MealsBotCommands getNewCommand(@NotNull String query) {
        try {
            return MealsBotCommands.getCommandByName(query);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    @Override
    public String getBotUsername() {
        return "MealsHelpBot";
    }

    @NotNull
    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

    @NotNull
    @Override
    public void sendReply(@NotNull SendMessage message) {
        try {
            message.setReplyMarkup(replyKeyboard.getKeyboardMarkup());
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

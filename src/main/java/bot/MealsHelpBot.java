package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import requests.*;

import java.util.HashMap;
import java.util.Map;

public class MealsHelpBot extends TelegramLongPollingBot implements ReplyCallback {

    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

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

    private Replier currentReplier = command2Replier.get(MealsBotCommands.NONE);

    private MealsReplyKeyboard replyKeyboard = new MealsReplyKeyboard();

    @Override
    public void onUpdateReceived(Update update) {
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

    private MealsBotCommands getNewCommand(String query) {
        try {
            return MealsBotCommands.getCommandByName(query);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return "MealsHelpBot";
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

    @Override
    public void sendReply(SendMessage message) {
        try {
            message.setReplyMarkup(replyKeyboard.getKeyboardMarkup());
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import requests.*;

import java.util.HashMap;
import java.util.Map;

public class MealsHelpBot extends TelegramLongPollingBot implements ReplyCallback {

    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    private static Map<Commands, Replier> command2Replier = new HashMap<>();

    {
        command2Replier.put(Commands.CAL, new CalReply(this));
        command2Replier.put(Commands.PFC, new PfcReply(this));
        command2Replier.put(Commands.RECOMMEND, new RecommendReply(this));
        command2Replier.put(Commands.NONE, new NoOpReply(this));

    }

    private Replier currentReplier = command2Replier.get(Commands.NONE);

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String query = update.getMessage().getText();
            if (query == null) {
                return;
            }

            query = query.toLowerCase();

            if (query.startsWith("/")) {
                Commands command = getNewCommand(query);
                currentReplier = command2Replier.get(command);
                if (currentReplier != null) { //todo else
                    currentReplier.initCall(update);
                }
            } else {
                currentReplier.reply(update);
            }
        }
    }


    private Commands getNewCommand(String query) {
        try {
            return Commands.getCommandByName(query);
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
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private enum Commands {
        CAL("/cal"), PFC("/pfc"), RECOMMEND("/recommend"), NONE("none");

        public final String name;

        Commands(String name) {
            this.name = name;
        }

        public String getCommandName() {
            return name;
        }

        public static Commands getCommandByName(String name) {
            if (name != null) {
                for (Commands command : values()) {
                    if (command.name.equals(name)) {
                        return command;
                    }
                }
            }
            return NONE;
        }
    }

}

import db.DbBackend;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class MealsHelpBot extends TelegramLongPollingBot {

    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    private static Map<Commands, CommandRunner> commands = new HashMap<>();

    static {
        commands.put(Commands.CAL, () -> String.valueOf(DbBackend.getProductCalories("milk")));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String answer;
            try {
                Commands command = Commands.getCommandByName(update.getMessage().getText());
                CommandRunner commandRunner = commands.get(command);
                if (commandRunner != null) {
                    answer = commandRunner.runCommand();
                } else {
                    answer = "Forgot to add command runner!";
                }
            } catch (IllegalArgumentException e) {
                answer = "Unknown command!";
            }

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(answer);
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "MealsHelpBot";
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }


    private enum Commands {
        CAL("/cal"), NONE("none");

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

    private interface CommandRunner {
        String runCommand();
    }

}

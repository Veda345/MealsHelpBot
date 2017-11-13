import db.DbBackend;
import db.ProductInfo;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class MealsHelpBot extends TelegramLongPollingBot {

    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    private static Commands currentCommand;

    private static Map<Commands, CommandRunner> commands2Runners = new HashMap<>();
    private static Map<Commands, String> commands2Questions = new HashMap<>();

    static {
        commands2Runners.put(Commands.CAL, productName -> {
            ProductInfo info = DbBackend.getProductCalories(productName);
            if (info != null) {
                return info.calories + " cal per serving (" + info.serving + ")";
            }
            return "Sorry, I don't know";
        });
        commands2Runners.put(Commands.PFC, productName -> {
            ProductInfo info = DbBackend.getProductPfc(productName);
            if (info != null) {
                return "Protein " + info.protein + "g | Fat " + info.fat + "g | Carbs " + info.carbs + "g | per serving (" + info.serving + ")";
            }
            return "Sorry, I don't know";
        });
        commands2Runners.put(Commands.NONE, productName -> "Try another command!");


        commands2Questions.put(Commands.CAL, "The caloric value of which product do you want to know?");
        commands2Questions.put(Commands.PFC, "The protein, fat and carb values of which product do you want to know?");
        commands2Questions.put(Commands.NONE, "Try another command!");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String answer;
            String query = update.getMessage().getText();
            if (query == null)  {
                return;
            }

            query = query.toLowerCase();

            if (query.startsWith("/")) {
                currentCommand = getNewCommand(query);
                answer = commands2Questions.get(currentCommand);
            } else {
                CommandRunner runner = commands2Runners.get(currentCommand);
                answer = runner.runCommand(query);
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


    private enum Commands {
        CAL("/cal"), PFC("/pfc"), NONE("none");

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
        String runCommand(String request);
    }

}

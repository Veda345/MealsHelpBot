package mealsbot.bot;

import com.sun.istack.internal.Nullable;
import mealsbot.requests.Replier;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MealsHelpBot extends TelegramLongPollingBot {

    private static final String API_TOKEN = "424486608:AAHfZOwoCJt4Iok87Xn7Q-MVGq3_AClwaFE";

    @Nullable
    private Replier currentReplier = command2Replier.get(MealsBotCommands.NONE);

    @NotNull
    private final static Map<MealsBotCommands, Replier> command2Replier = new ConcurrentHashMap<>();

    public MealsHelpBot(@NotNull List<Replier> repliers) {
        command2Replier.putAll(repliers.stream().collect(Collectors.toMap(Replier::getReplierType, Function.identity())));
    }

    public void initRepliers() {
        ReplyCallback callback = new ReplyCallback(this);
        for (Replier replier: command2Replier.values()) {
            replier.setReplyCallback(callback);
        }
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

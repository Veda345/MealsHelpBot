package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utils.SingletonsCreator;

import javax.validation.constraints.NotNull;

public class BotRunner {

    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(BotRunner.class);

    public static void main(String[] args) {
        startBot();
    }

    private static void startBot() {
        logger.info("Starting bot");
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(SingletonsCreator.mealsHelpBot());
        } catch (TelegramApiException e) {
            logger.error("Error while registering bot", e);
        }
    }
}

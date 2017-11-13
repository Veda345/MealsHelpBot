import db.DbBackend;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class BotRunner {

    public static void main(String[] args) {
        initDb();
        startBot();
    }

    private static void initDb() {
        DbBackend.dropTable();
        DbBackend.createTableIfNotExist();
        DbBackend.addProductCaloriesInfo("milk", 100);
    }

    private static void startBot() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new MealsHelpBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

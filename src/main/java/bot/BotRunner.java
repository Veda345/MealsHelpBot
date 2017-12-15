package bot;

import db.DbBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utils.SingletonsCreator;

import javax.validation.constraints.NotNull;
import java.io.*;

public class BotRunner {

    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(BotRunner.class);

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Init db? [y/n]");
            String ans = reader.readLine();
            switch (ans) {
                case "y" :
                    initDb();
                    break;
                case "n" :
                    break;
                default:
                    System.out.println("You've typed something strange, won't init");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        startBot();
    }

    private static void initDb() {
        DbBackend.dropTables();
        DbBackend.createTables();
        readDatabase();
    }

    private static void readDatabase() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("food_database")));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String[] parts = line.split("\t");
                String name = parts[0].split(",")[0];

                DbBackend.addProductCaloriesInfo(name, parts[1], Long.parseLong(parts[3]));

                DbBackend.addProductPfcInfo(name, parts[1], Long.parseLong(parts[6]),  Long.parseLong(parts[4]),  Long.parseLong(parts[5]));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

import db.DbBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import utils.RecommendCache;

import java.io.*;

public class BotRunner {

    private final static Logger logger = LoggerFactory.getLogger(BotRunner.class);


    public static void main(String[] args) {
        initDb();
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

//                System.out.println(name + " " + parts[1] + " " + Long.parseLong(parts[3]));
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
            telegramBotsApi.registerBot(new MealsHelpBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

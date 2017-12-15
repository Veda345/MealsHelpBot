package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Drops and creates tables, then fills them with some data
 */
public class DatabaseInitializer {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static void main(String[] args) {
        initDb();
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

                DbBackend.addProductPfcInfo(name, parts[1], Long.parseLong(parts[6]), Long.parseLong(parts[4]), Long.parseLong(parts[5]));
            }

            br.close();
        } catch (IOException e) {
            logger.error("Error while filling tables with data", e);
        }
    }
}

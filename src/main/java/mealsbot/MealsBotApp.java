package mealsbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.validation.constraints.NotNull;

@SpringBootApplication
@Configuration
public class MealsBotApp {
    @NotNull
    private final static Logger logger = LoggerFactory.getLogger(MealsBotApp.class);

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(MealsBotApp.class, args);
        logger.info("Bot has started");
    }
}

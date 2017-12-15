package mealsbot;

import mealsbot.bot.MealsHelpBot;
import mealsbot.data.RecommendCache;
import mealsbot.http.JSONParser;
import mealsbot.http.RecipesRequester;
import mealsbot.requests.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(MealsBotApp.class);

    @Bean
    public AddToFavReply addToFavReply() {
        return new AddToFavReply(recommendCache());
    }

    @Bean
    public CalReply calReply() {
        return new CalReply();
    }

    @Bean
    public ClearReply clearReply() {
        return new ClearReply(recommendCache());
    }

    @Bean
    public FavReply favReply() {
        return new FavReply();
    }

    @Bean
    public FindReply findReply() {
        return new FindReply(recipesRequester());
    }

    @Bean
    public HelpReply helpReply() {
        return new HelpReply();
    }

    @Bean
    public NoOpReply noOpReply() {
        return new NoOpReply();
    }

    @Bean
    public PfcReply pfcReply() {
        return new PfcReply();
    }

    @Bean
    public RecommendReply recommendReply() {
        return new RecommendReply(recipesRequester(), recommendCache());
    }

    @Bean
    public RecommendCache recommendCache() {
        return new RecommendCache();
    }

    @Bean
    public RecipesRequester recipesRequester() {
        return new RecipesRequester(jsonParser());
    }

    @Bean
    public List<Replier> repliers() {
        List<Replier> repliers = new ArrayList<>();
        repliers.add(calReply());
        repliers.add(pfcReply());
        repliers.add(recommendReply());
        repliers.add(noOpReply());
        repliers.add(helpReply());
        repliers.add(addToFavReply());
        repliers.add(favReply());
        repliers.add(clearReply());
        repliers.add(findReply());
        return repliers;
    }

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser();
    }

    @Bean
    public MealsHelpBot mealsHelpBot() {
        MealsHelpBot mealsHelpBot = new MealsHelpBot(repliers());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(mealsHelpBot);
        } catch (TelegramApiException e) {
            logger.error("Error while registering bot", e);
            throw new RuntimeException(e);
        }
        return mealsHelpBot;
    }
}

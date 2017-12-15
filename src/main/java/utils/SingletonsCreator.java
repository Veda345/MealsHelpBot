package utils;

import bot.MealsHelpBot;
import data.RecommendCache;

import javax.validation.constraints.NotNull;

public class SingletonsCreator {

    @NotNull
    private static RecommendCache recommendCache = new RecommendCache();

    @NotNull
    private static MealsHelpBot mealsHelpBot = new MealsHelpBot();

    @NotNull
    public static RecommendCache recommendCache() {
        return recommendCache;
    }

    public static MealsHelpBot mealsHelpBot() {
        return mealsHelpBot;
    }
}

package utils;

import bot.MealsHelpBot;
import data.RecommendCache;
import requests.*;

import java.util.ArrayList;
import java.util.List;

public class SingletonsCreator {

    private static RecommendCache recommendCache = new RecommendCache();

    private static MealsHelpBot mealsHelpBot = new MealsHelpBot();

    private static List<Replier> repliers;

    public static RecommendCache recommendCache() {
        return recommendCache;
    }

    public static MealsHelpBot mealsHelpBot() {
        return mealsHelpBot;
    }

    public static List<Replier> getRepliers() {
        if (repliers == null) {
            repliers = new ArrayList<>();
            repliers.add(new CalReply());
            repliers.add(new PfcReply());
            repliers.add(new RecommendReply());
            repliers.add(new NoOpReply());
            repliers.add(new HelpReply());
            repliers.add(new AddToFavReply());
            repliers.add(new FavReply());
            repliers.add(new ClearReply());
            return repliers;
        }
        else {
            return repliers;
        }
    }
}

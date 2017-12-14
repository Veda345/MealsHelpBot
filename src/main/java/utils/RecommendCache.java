package utils;

import http.Recipe;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendCache {

    /**
     * Cache that contains last recommended recipe to telegram user,
     * doesn't survive reboot
     */
    @NotNull
    private final Map<Integer, Recipe> lastRecommended = new ConcurrentHashMap<>();

    public void addRecommended(@NotNull Integer personId, @NotNull Recipe recipe) {
        lastRecommended.put(personId, recipe);
    }

    @NotNull
    public Recipe getRecommended(@NotNull Integer personId) {
        return lastRecommended.get(personId);
    }

    public void deleteRecommended(@NotNull Integer personId) {
        lastRecommended.remove(personId);
    }
}

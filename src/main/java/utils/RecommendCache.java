package utils;

import http.Recipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendCache {

    private final Map<Integer, Recipe> lastRecommended = new ConcurrentHashMap<>();

    public void addRecommended(Integer personId, Recipe recipe) {
        lastRecommended.put(personId, recipe);
    }

    public Recipe getRecommended(Integer personId) {
        return lastRecommended.get(personId);
    }

    public void deleteRecommended(Integer personId) {
        lastRecommended.remove(personId);
    }
}

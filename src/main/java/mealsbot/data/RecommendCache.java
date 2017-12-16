package mealsbot.data;

import com.sun.istack.internal.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendCache {

    /**
     * Cache that contains last recommended recipe to telegram user,
     * doesn't survive reboot
     */
    private final Map<Integer, Recipe> lastRecommended = new ConcurrentHashMap<>();

    public void addRecommended(@NotNull Integer personId, @Nullable Recipe recipe) {
        if (recipe != null) {
            lastRecommended.put(personId, recipe);
        }
        else {
            lastRecommended.remove(personId);
        }
    }

    @Nullable
    public Recipe getRecommended(@NotNull Integer personId) {
        return lastRecommended.get(personId);
    }

    public void deleteRecommended(@NotNull Integer personId) {
        lastRecommended.remove(personId);
    }
}

package utils;

import javax.validation.constraints.NotNull;

public class BeanCreator {

    @NotNull
    private static RecommendCache recommendCache = new RecommendCache();

    @NotNull
    public static RecommendCache recommendCache() {
        return recommendCache;
    }
}

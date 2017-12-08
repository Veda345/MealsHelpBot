package utils;

public class BeanCreator {

    private static RecommendCache recommendCache = new RecommendCache();

    public static RecommendCache recommendCache() {
        return recommendCache;
    }
}

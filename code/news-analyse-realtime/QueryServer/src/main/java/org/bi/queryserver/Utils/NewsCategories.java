package org.bi.queryserver.Utils;

public final class NewsCategories {
    public static final String[] CATEGORIES = {
            "sports",
            "news",
            "autos",
            "foodanddrink",
            "finance",
            "music",
            "lifestyle",
            "weather",
            "health",
            "video",
            "movies",
            "tv",
            "travel",
            "entertainment",
            "kids",
            "europe",
            "northamerica",
            "adexperience"
    };

    // 防止实例化
    private NewsCategories() {
        throw new AssertionError("Cannot instantiate this class");
    }
}

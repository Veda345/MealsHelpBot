package utils;

import javax.validation.constraints.NotNull;

public class FormattingUtils {
    private FormattingUtils() {

    }

    public static String formatTitle(@NotNull String title) {
        return "<b>" + title + "</b>";
    }

    public static String formatBoldText(@NotNull String title) {
        return "<b>" + title + "</b>";
    }

    public static String formatPointedText(@NotNull String title) {
        return "✻ " + title + "\n";
    }
}

package mealsbot.bot;

import javax.validation.constraints.NotNull;

public enum MealsBotCommands {
    CAL("/cal"),
    PFC("/pfc"),
    RECOMMEND("/recommend"),
    FAV("/fav"),
    HELP("/help"),
    ADDTOFAV("/addtofav"),
    CLEAR("/clear"),
    FIND("/find"),
    NONE("none");

    public final String name;

    MealsBotCommands(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getCommandName() {
        return name;
    }

    @NotNull
    public static MealsBotCommands getCommandByName(@NotNull String name) {
        if (name != null) {
            for (MealsBotCommands command : values()) {
                if (command.name.equals(name)) {
                    return command;
                }
            }
        }
        return NONE;
    }
}

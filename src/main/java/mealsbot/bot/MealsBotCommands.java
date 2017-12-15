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

    @NotNull
    public final String name;

    MealsBotCommands(@NotNull String name) {
        this.name = name;
    }

    public String getCommandName() {
        return name;
    }

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

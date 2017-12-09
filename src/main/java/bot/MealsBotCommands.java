package bot;

public enum MealsBotCommands {
    CAL("/cal"),
    PFC("/pfc"),
    RECOMMEND("/recommend"),
    FAV("/fav"),
    HELP("/help"),
    ADDTOFAV("/addtofav"),
    CLEAR("/clear"),
    NONE("none");

    public final String name;

    MealsBotCommands(String name) {
        this.name = name;
    }

    public String getCommandName() {
        return name;
    }

    public static MealsBotCommands getCommandByName(String name) {
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

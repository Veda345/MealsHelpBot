package mealsbot.db;

import javax.validation.constraints.NotNull;

interface DbContract {

    @NotNull
    String DATABASE = "jdbc:sqlite:food.mealsbot.db";

    @NotNull
    String CALORIES = "CALORIES";

    interface CaloriesTable {
        String ID = "id";
        String NAME = "name";
        String CAL = "cal";
        String SERVING = "serving";
    }

    String PFC = "PFC";
    interface PfcTable {
        String ID = "id";
        String NAME = "name";
        String PROTEIN = "protein";
        String FAT = "fat";
        String CARBS = "carbs";
        String SERVING = "serving";
    }

    String FAVRECIPE = "FAVRECIPE";
    interface FavRecipeTable {
        String ID = "id";
        String RECIPEID = "recipeid";
        String PERSONID = "personid";
        String TITLE = "title";
        String TIME = "time";
        String ENERGY = "energy";
        String IMGURL = "imgUrl";
        String STEPS = "steps";
    }
}
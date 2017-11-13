package db;

interface DbContract {

    String DATABASE = "jdbc:sqlite:food.db";

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
}
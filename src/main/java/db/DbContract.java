package db;

interface DbContract {

    String DATABASE = "jdbc:sqlite:temp.db";

    String CALORIES = "CALORIES";
    interface CaloriesTable {
        String ID = "id";
        String NAME = "name";
        String CAL = "cal";
    }
}
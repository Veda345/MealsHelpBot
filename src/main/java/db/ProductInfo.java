package db;

import javax.validation.constraints.NotNull;

public class ProductInfo {

    @NotNull
    public String name;
    @NotNull
    public String serving;
    public long calories;
    public long fat, carbs, protein;

    ProductInfo(String name, String serving, long calories) {
        this.name = name;
        this.serving = serving;
        this.calories = calories;
    }

    ProductInfo(String name, String serving, long protein, long fat, long carbs) {
        this.name = name;
        this.serving = serving;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
}

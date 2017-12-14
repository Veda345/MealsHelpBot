package data;

import com.sun.istack.internal.Nullable;

import javax.validation.constraints.NotNull;

public class ProductInfo {

    @NotNull
    public String name;
    @NotNull
    public String serving;
    public long calories;
    public long fat, carbs, protein;

    ProductInfo(String name, String serving, long calories, long fat, long carbs, long protein) {
        this.name = name;
        this.serving = serving;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
    }

    public static class Builder {

        @NotNull
        String name;

        @NotNull
        String serving;

        @Nullable
        long calories;

        @Nullable
        long fat;

        @Nullable
        long carbs;

        @Nullable
        long protein;

        @NotNull
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder serving(String serving) {
            this.serving = serving;
            return this;
        }

        @NotNull
        public Builder calories(long calories) {
            this.calories = calories;
            return this;
        }

        @NotNull
        public Builder fat(long fat) {
            this.fat = fat;
            return this;
        }

        @NotNull
        public Builder carbs(long carbs) {
            this.carbs = carbs;
            return this;
        }

        @NotNull
        public Builder protein(long protein) {
            this.protein = protein;
            return this;
        }

        @NotNull
        public ProductInfo build() {
            return new ProductInfo(name, serving, calories, fat, carbs, protein);
        }
    }
}

package data;

import javax.validation.constraints.NotNull;

public class ProductInfo {

    @NotNull
    public String name;
    @NotNull
    public String serving;

    public long calories;

    public long fat;

    public long carbs;

    public long protein;

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
        private String name;

        @NotNull
        private String serving;

        private long calories;

        private long fat;

        private long carbs;

        private long protein;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder serving(String serving) {
            this.serving = serving;
            return this;
        }

        public Builder calories(long calories) {
            this.calories = calories;
            return this;
        }

        public Builder fat(long fat) {
            this.fat = fat;
            return this;
        }

        public Builder carbs(long carbs) {
            this.carbs = carbs;
            return this;
        }

        public Builder protein(long protein) {
            this.protein = protein;
            return this;
        }

        public ProductInfo build() {
            return new ProductInfo(name, serving, calories, fat, carbs, protein);
        }
    }
}

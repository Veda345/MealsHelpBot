package mealsbot.data;

import com.sun.istack.internal.Nullable;

import javax.validation.constraints.NotNull;

public class ProductInfo {

    @NotNull
    public String name;

    @NotNull
    public String serving;

    @Nullable
    public Long calories;

    @Nullable
    public Long fat;

    @Nullable
    public Long carbs;

    @Nullable
    public Long protein;

    private ProductInfo(@NotNull String name, @NotNull String serving, @Nullable Long calories, @Nullable Long fat,
                @Nullable Long carbs, @Nullable Long protein) {
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

        @Nullable
        private Long calories;

        @Nullable
        private Long fat;

        @Nullable
        private Long carbs;

        @Nullable
        private Long protein;

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

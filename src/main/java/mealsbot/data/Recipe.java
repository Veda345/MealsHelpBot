package mealsbot.data;

import com.google.common.annotations.VisibleForTesting;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class Recipe {

    @NotNull
    public String id;

    @NotNull
    public String title;

    @Nullable
    public String imgUrl;

    @Nullable
    public List<Stage> stages;

    @Nullable
    public Integer time;

    @Nullable
    public Integer energy;

    @VisibleForTesting
    public Recipe(@NotNull String id, @NotNull String title, @Nullable Integer time, @Nullable Integer energy,
                  @Nullable String imgUrl, @Nullable List<Stage> stages) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.energy = energy;
        this.imgUrl = imgUrl;
        this.stages = stages;
    }

    public static class Stage {
        @NotNull
        public List<String> steps;
        @Nullable
        public String imgUrl;

        public Stage(@NotNull List<String> steps, @Nullable String imgUrl) {
            this.steps = steps;
            this.imgUrl = imgUrl;
        }
    }

    public static class Builder {
        @NotNull
        private String id;

        @NotNull
        private String title;

        @Nullable
        private String imgUrl;

        @Nullable
        private List<Stage> stages;

        @Nullable
        private Integer time;

        @Nullable
        private Integer energy;

        @NotNull
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }

        @NotNull
        public Builder title(@NotNull String title) {
            this.title = title;
            return this;
        }

        @NotNull
        public Builder imgUrl(@NotNull String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }


        @NotNull
        public Builder stages(@NotNull List<Stage> stages) {
            this.stages = stages;
            return this;
        }


        @NotNull
        public Builder time(int time) {
            this.time = time;
            return this;
        }


        @NotNull
        public Builder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @NotNull
        public Recipe build() {
            return new Recipe(id, title, time, energy, imgUrl, stages);
        }
    }
}

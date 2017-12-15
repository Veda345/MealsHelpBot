package data;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class Recipe {

    @NotNull
    public String id;

    @NotNull
    public String title;

    public String imgUrl;

    public List<Stage> stages;

    public int time;

    public int energy;

    Recipe(@NotNull String id, @NotNull String title, int time, int energy, @NotNull String imgUrl, @NotNull List<Stage> stages) {
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
        @NotNull
        public String imgUrl;

        public Stage(@NotNull List<String> steps, @Nullable String imgUrl) {
            this.steps = steps;
            this.imgUrl = imgUrl;
        }
    }

    public static class Builder {

        @Nullable
        private String id;
        @Nullable
        private String title;
        @Nullable
        private String imgUrl;
        @Nullable
        private List<Stage> stages;
        private int time;
        private int energy;

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

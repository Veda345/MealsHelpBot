package http;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class Recipe {

    @NotNull
    public String id;
    @NotNull
    public String title;
    public int time;
    public int energy;
    @Nullable
    public String imgUrl;
    @Nullable
    public List<Stage> stages;

    public Recipe(@NotNull String id, @NotNull String title, int time, int energy, @NotNull String imgUrl) {
        this(id, title, time, energy, imgUrl, null);
    }

    public Recipe(@NotNull String id, @NotNull String title, int time, int energy, @NotNull String imgUrl, @NotNull List<Stage> stages) {
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

        Stage(@NotNull List<String> steps, @Nullable String imgUrl) {
            this.steps = steps;
            this.imgUrl = imgUrl;
        }
    }
}

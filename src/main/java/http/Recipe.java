package http;

import com.sun.istack.internal.Nullable;

import java.util.List;

public class Recipe {

    @Nullable
    public String id;
    @Nullable
    public String title;
    @Nullable
    public int time;
    @Nullable
    public int energy;
    @Nullable
    public String imgUrl;
    @Nullable
    public List<Stage> stages;

    public Recipe(String id, String title, int time, int energy, String imgUrl) {
        this(id, title, time, energy, imgUrl, null);
    }

    public Recipe(String id, String title, int time, int energy, String imgUrl, List<Stage> stages) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.energy = energy;
        this.imgUrl = imgUrl;
        this.stages = stages;
    }

    public static class Stage {
        public List<String> steps;
        public String imgUrl;

        Stage(List<String> steps, String imgUrl) {
            this.steps = steps;
            this.imgUrl = imgUrl;
        }
    }
}

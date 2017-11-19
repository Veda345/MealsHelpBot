package http;

import com.sun.istack.internal.Nullable;

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

    Recipe(String id, String title, int time, int energy, String imgUrl) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.energy = energy;
        this.imgUrl = imgUrl;
    }

}

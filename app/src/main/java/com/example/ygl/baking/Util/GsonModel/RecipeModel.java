package com.example.ygl.baking.Util.GsonModel;

import java.util.List;

/**
 * Created by YGL on 2017/6/21.
 */

public class RecipeModel {
    public String id;
    public String name;
    public List<IngredientsModel> ingredients;
    public List<StepsModel> steps;
    public String servings;
    public String image;

    public static class IngredientsModel {
        public String quantity;
        public String measure;
        public String ingredient;
    }
    public static class StepsModel {
        public int id;
        public String shortDescription;
        public String description;
        public String videoURL;
        public String thumbnailURL;
    }
}

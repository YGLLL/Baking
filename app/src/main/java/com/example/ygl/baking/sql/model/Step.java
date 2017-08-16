package com.example.ygl.baking.sql.model;

import org.litepal.crud.DataSupport;

/**
 * Created by YGL on 2017/6/20.
 */
//litepal需要的模型
public class Step extends DataSupport {
    private String ForRecipe;
    private String StepId;
    private String StepTitle;
    private String Description;
    private String VideoUrl;

    public String getForRecipe() {
        return ForRecipe;
    }

    public void setForRecipe(String forRecipe) {
        ForRecipe = forRecipe;
    }

    public String getStepTitle() {
        return StepTitle;
    }

    public void setStepTitle(String stepTitle) {
        StepTitle = stepTitle;
    }

    public String getStepId() {
        return StepId;
    }

    public void setStepId(String stepId) {
        StepId = stepId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}

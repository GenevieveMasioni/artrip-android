package com.example.pjs4_app;

import android.graphics.drawable.Drawable;

public class ScreenItem {

    private String title, description;
    private Drawable Screenimg;

    /**
     * Build a slide
     * @param title
     * @param description
     * @param img
     */
    public ScreenItem(String title, String description, Drawable img){
        this.title = title;
        this.description = description;
        this.Screenimg = img;

    }

    /**
     * Gets the slide title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the slide description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the slide background
     * @return
     */
    public Drawable getScreenimg() {
        return Screenimg;
    }
}

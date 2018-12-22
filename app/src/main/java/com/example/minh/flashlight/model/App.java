package com.example.minh.flashlight.model;

import android.graphics.drawable.Drawable;

public class App {
    private String name;
    private Drawable image;
    private Boolean isChoose;
    private String pakageName;

    public App(String name, Drawable image, Boolean isChoose, String pakageName) {
        this.name = name;
        this.image = image;
        this.isChoose = isChoose;
        this.pakageName = pakageName;
    }

    public App(String name, Boolean isChoose) {
        this.name = name;
        this.isChoose = isChoose;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Boolean getChoose() {
        return isChoose;
    }

    public void setChoose(Boolean choose) {
        isChoose = choose;
    }

    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String pakageName) {
        this.pakageName = pakageName;
    }
}

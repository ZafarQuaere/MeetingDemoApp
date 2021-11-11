package com.pgi.convergencemeetings.models;

/**
 * Created by amit1829 on 11/14/2017.
 */

public class About {

    private int icon;
    private String title;

    public About(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

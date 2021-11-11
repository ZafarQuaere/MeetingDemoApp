package com.pgi.convergencemeetings.models;

/**
 * Created by amit1829 on 7/4/2016.
 */
public class NavigationDrawerItem {

    private int itemType;
    private String avatar;
    private int icon;
    private String title;
    private String email;

    public NavigationDrawerItem(int itemType, String avatar, int icon, String title, String email) {
        this.itemType = itemType;
        this.avatar = avatar;
        this.icon = icon;
        this.title = title;
        this.email = email;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

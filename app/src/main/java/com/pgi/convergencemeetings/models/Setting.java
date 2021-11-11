package com.pgi.convergencemeetings.models;

/**
 * Created by amit1829 on 11/14/2017.
 */

public class Setting {

    private int mIcon;
    private String mTitle;
    private String mSubTitle;
    private boolean mIsMultiLabel;

    public Setting(int icon, String title, boolean isMultiLabel, String subTitle) {
        this.mIcon = icon;
        this.mTitle = title;
        this.mIsMultiLabel = isMultiLabel;
        this.mSubTitle = subTitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public boolean isMultiLabel() {
        return mIsMultiLabel;
    }

    public void setMultiLabel(boolean multiLabel) {
        mIsMultiLabel = multiLabel;
    }

    public String getmSubTitle() {
        return mSubTitle;
    }

    public void setmSubTitle(String mSubTitle) {
        this.mSubTitle = mSubTitle;
    }
}

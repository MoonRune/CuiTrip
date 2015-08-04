package com.cuitrip.model;

/**
 * Created on 7/30.
 */
public class SavedLocalService implements ServiceListInterface{
    public String title;
    public String content;
    public String mainPic;

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getDescpt() {
        return content;
    }

    public String getBackPic(){
        return mainPic;
    }
}

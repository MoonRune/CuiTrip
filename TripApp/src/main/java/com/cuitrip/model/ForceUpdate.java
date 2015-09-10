package com.cuitrip.model;

/**
 * Created by baziii on 15/8/24.
 */
public class ForceUpdate {
    private boolean needUpdate;

    public String getUrl() {
        return "http://www.cuitrip.com";
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }
}

package com.cuitrip.model;

import java.util.List;

/**
 * Created by baziii on 15/8/19.
 */
public class LocationMode {
    private List<AreaMode> content;
    private String locationType;
    private String lang;
    private String country;

    public List<AreaMode> getContent() {
        return content;
    }

    public void setContent(List<AreaMode> content) {
        this.content = content;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

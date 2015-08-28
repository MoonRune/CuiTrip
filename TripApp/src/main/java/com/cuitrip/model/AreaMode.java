package com.cuitrip.model;

/**
 * Created by baziii on 15/8/19.
 */
public class AreaMode {
    private String name;
    private String locationId;
    private String abbr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaMode areaMode = (AreaMode) o;

        if (locationId != null ? !locationId.equals(areaMode.locationId) : areaMode.locationId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return locationId != null ? locationId.hashCode() : 0;
    }
}

package com.lab.location;

/**
 * Created on 7/30.
 */
public class CtLocation {

    public CtLocation(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double longitude;

    public double latitude;

    //TODO:
    public static final CtLocation DEFAULT_LOCATION = new CtLocation(0, 0);

}

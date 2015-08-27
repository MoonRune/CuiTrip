package com.lab.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.cuitrip.app.MainApplication;
import com.lab.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/30.
 */
public class LocationHelper {

    private static final String TAG = "LocationHelper";

    private static CtLocation sLocation;

    private static volatile boolean sUpdating = false;

    private static List<OnLocationUpdatedCallback> sCallbacks = new ArrayList<OnLocationUpdatedCallback>();

    public synchronized static CtLocation getLoation(final OnLocationUpdatedCallback callback) {
        if (sLocation != null) {
            return sLocation;
        }

        if (sUpdating) {
            if (callback != null) {
                sCallbacks.add(callback);
            }
            return null;
        }
        // Or, use GPS location data:
        // String locationProvider = LocationManager.GPS_PROVIDER;
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) MainApplication.getInstance()
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates

        boolean findProvider = false;
        List<String> providers = locationManager.getAllProviders();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                if ((LocationManager.GPS_PROVIDER.equals(provider)
                        && locationManager.isProviderEnabled(provider))
                        || (LocationManager.NETWORK_PROVIDER.equals(provider)
                        && locationManager.isProviderEnabled(provider))) {
                    // Register the listener with the Location Manager to receive location updates
                    locationManager.requestLocationUpdates(provider, 1000, 1000, locationListener);
                    findProvider = true;
                }
            }
        }
        if (!findProvider) {
            return CtLocation.DEFAULT_LOCATION;
        } else {
            if (callback != null) {
                sCallbacks.add(callback);
            }
            sUpdating = true;
        }
        return null;
    }

    public synchronized static CtLocation getLoation() {
        return sLocation == null ? CtLocation.DEFAULT_LOCATION : sLocation;
    }

    public interface OnLocationUpdatedCallback {
        void onLocationUpdated(CtLocation location);
    }

    private static final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                sLocation = new CtLocation(location.getLongitude(), location.getLatitude());
            }
            LogHelper.d(TAG, "new location: " + location.getLatitude()
                    + "/" + location.getLongitude());
            if (!sCallbacks.isEmpty()) {
                for (OnLocationUpdatedCallback callback1 : sCallbacks) {
                    callback1.onLocationUpdated(sLocation);
                }
            }
            sCallbacks.clear();
            final LocationManager locationManager = (LocationManager) MainApplication.getInstance()
                    .getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            LogHelper.d(TAG, "onStatusChanged");
        }

        public void onProviderEnabled(String provider) {
            LogHelper.d(TAG, "onProviderEnabled");
        }

        public void onProviderDisabled(String provider) {
            LogHelper.d(TAG, "onProviderDisabled");
        }
    };

    public static void closeLocation() {
        final LocationManager locationManager = (LocationManager) MainApplication.getInstance()
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }
}

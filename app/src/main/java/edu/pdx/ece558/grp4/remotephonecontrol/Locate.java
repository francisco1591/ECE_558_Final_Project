package edu.pdx.ece558.grp4.remotephonecontrol;

/**
 * Created by Francisco on 6/9/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Locate {
    private static final String TAG ="LocateClass";
    private double LocTimeOut = 15e3; //location timeout ms
    private int MinLocAccuracy = 12; // min accuracy radius in meters
    private LocationManager lm;
    private LocationListener mLL;
    Location mLocation;
    double mLong; // Longitude in degrees
    double mLat; // Latitude in degrees
    float mRadius; // Accuracy 68% radius, in meters
    long mTime0; // Time stamp of initial location attempt (ms)
    long mLocElapsedTime; // ms since initial Location attempt
    int mLocAttempts;

    public Locate(Context context, double timeout, int radius){
        LocTimeOut = timeout;
        MinLocAccuracy = radius;
        mLocAttempts = 0;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void addListener() {
        // Define a listener that responds to location updates
        mLL = new LocationListener() {
            // Called when a new location is found
            public void onLocationChanged(Location location) {
                String s;
                // if first attempt
                if (mLocAttempts==0) {
                    mTime0 = (long) (location.getElapsedRealtimeNanos()/1e6);
                }
                mLocElapsedTime = (long) (location.getElapsedRealtimeNanos()/1e6) - mTime0;
                mLocAttempts++;
                // If found accurate location or timed out, stop listener and store location
                if ( mLocElapsedTime > LocTimeOut ||
                        ( location.hasAccuracy() && location.getAccuracy() <= MinLocAccuracy ) ) {
                    stopLocationUpdates();
                    saveLocation(location);
//                    s = "New location:" +
//                            "\nLatitude:"+mLat+
//                            "\nLongitude:"+mLong+
//                            "\nRadius:"+mRadius+
//                            "\nTime:"+mLocElapsedTime/1000.0;
//                    mLocTV.setText( s );
                }
//                else {
//                    s = "Time:" + mLocElapsedTime / 1000.0;
//                    mLocTV.setText(s);
//                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }

    private void saveLocation(Location location) {
        mLocation = location;
        mLong = location.getLongitude();
        mLat = location.getLatitude();
        mRadius = location.getAccuracy();
    }

    // Stop location updates and reset attempts
    private void stopLocationUpdates() {
        lm.removeUpdates(mLL);
        mLocAttempts = 0; // reset attempts
    }

    // Get device location, returns Location object
    // In reference to the @TargetAPI(23) statement below,
    // note that requestPermissions will only be called with
    // APIs 23 and greater since older APIs got permission during app installation
//    @TargetApi(23)
//    public void getLocationUpdates() {
//        if (hasFineLocationPermission()) {
//            // Acquire a reference to the system Location Manager
//            try {
//                boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                // Register the listener with the Location Manager to receive location updates
//                if (isNetworkEnabled) {
//                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLL);
//                }
//                if (isGPSEnabled) {
//                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLL);
//                }
//            } catch (SecurityException sex) {
//                Log.e(TAG, "Error creating location service: " + sex.getMessage());
//            }
//        } else {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSION_FINE_LOCATION);
//        }
//    }


}

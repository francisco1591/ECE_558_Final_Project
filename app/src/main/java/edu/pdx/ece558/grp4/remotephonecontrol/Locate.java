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
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

// Rehan adding a test comment for pushing demo
public class Locate {
    private static final String TAG = "LocateClass";
    private double LocTimeOut; //location timeout ms
    private int MinLocAccuracy; // min accuracy radius in meters
    private LocationManager lm;
    private LocationListener mLL;
    Location mBestLocation;
    double mLong; // Longitude in degrees
    double mLat; // Latitude in degrees
    float mRadius; // Accuracy 68% radius, in meters
    long mTime0; // Time stamp of initial location attempt (ms)
    long mLocElapsedTime; // ms since initial Location attempt
    int mLocAttempts;
    boolean mFoundLocation;
    Context mContext;

    public Locate(Context context, double timeout, int radius) {
        mContext = context;
        LocTimeOut = timeout;
        MinLocAccuracy = radius;
        mLocAttempts = 0;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        addListener();
    }

    public void addListener() {
        // Define a listener that responds to location updates
        mLL = new LocationListener() {
            // Called when a new location is found
            public void onLocationChanged(Location location) {
                String s;
                mFoundLocation = true;
                // if first attempt
                if (mLocAttempts == 0) {
                    mTime0 = (long) (location.getElapsedRealtimeNanos() / 1e6);
                }
                mLocElapsedTime = (long) (location.getElapsedRealtimeNanos() / 1e6) - mTime0;
                mLocAttempts++;
                if (mBestLocation != null && location.getAccuracy() < mBestLocation.getAccuracy()) {
                    saveLocation(location);
                }
                // If found accurate location or timed out, stop listener and store location
                if (mLocElapsedTime > LocTimeOut ||
                        (location.hasAccuracy() && location.getAccuracy() <= MinLocAccuracy)) {
                    stopLocationUpdates();
                    saveLocation(location);
                    s = "New location:" +
                            "\nLatitude, Longitude: " + mLat + ", " + mLong +
                            "\nRadius:" + mRadius +
                            "\nTime:" + mLocElapsedTime / 1000.0;
                    ((SMSListener)mContext).replyToSender(s,null);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void getLastKnownLocation() {
        DecimalFormat df = new DecimalFormat("#.##");
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            saveLocation(location);
        }  catch (SecurityException sex) {
            Log.e(TAG, "Error creating location service: " + sex.getMessage());
        }
        double age_min = (SystemClock.elapsedRealtimeNanos()-mBestLocation.getElapsedRealtimeNanos() ) / 60.0e9;
        String age = df.format(age_min);
        String s = "Last known location:" +
                "\nLatitude, Longitude: " + mLat + ", " + mLong +
                "\nRadius:" + mRadius +
                "\nAge: "+ age + " minutes ago.";
        ((SMSListener)mContext).replyToSender(s,null);
    }

    private void saveLocation(Location location) {
        mBestLocation = location;
        mLong = location.getLongitude();
        mLat = location.getLatitude();
        mRadius = location.getAccuracy();
    }

    // Stop location updates and reset attempts
    private void stopLocationUpdates() {
        lm.removeUpdates(mLL);
        mLocAttempts = 0; // reset attempts
    }

    // Get device location
    public void getLocationUpdates() {
        mFoundLocation = false;
        try {
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // Register the listener with the Location Manager to receive location updates
            if (isNetworkEnabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLL);
            }
            if (isGPSEnabled) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLL);
            }
        } catch (SecurityException sex) {
            Log.e(TAG, "Error creating location service: " + sex.getMessage());
        }
    }
}

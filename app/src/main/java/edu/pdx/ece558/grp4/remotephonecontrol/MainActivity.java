package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.Manifest;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.*;

//////////////////
// MainActivity //
//////////////////

public class MainActivity extends AppCompatActivity {

    // Tag to identify this activity in logcat
    private static final String TAG = "RemotePhoneControl";

    // File to save SharedPreferences in
    public static final String PREFS_NAME ="RemotePhoneControl";

    private static final int REQUEST_SMS_PERMISSION = 0;
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;

    // Private members
    boolean mSMSControl;
    boolean mEmailResponse;
    boolean mRemoteLocation;

    // UI Widgets
    Button btnStart;
    Button btnStop;

    ToggleButton toggleSMS;
    ToggleButton toggleEmail;
    ToggleButton toggleLocation;

    //////////////
    // onCreate //
    //////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the permissions for SMS & GPS
        getSMSpermissions();
        getLocationPermission();

        // Load the previous values for user preference...
        // i.e. whether SMS control, email control, location are allowed
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        mSMSControl = settings.getBoolean("SMSControl", false);
        mEmailResponse = settings.getBoolean("EmailControl", false);
        mRemoteLocation = settings.getBoolean("RemoteLocation", false);

        // Wire up the button to start service
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SMSListener.class);
                startService(intent);
            } // onClick

        }); // OnClickListener

        // Wire up the button to stop service
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SMSListener.class);
                stopService(intent);
            } // onClick

        }); // OnClickListener

        // Wire up the toggle to enable SMS control
        toggleSMS = (ToggleButton) findViewById(R.id.toggle_SMS);
        toggleSMS.setChecked(mSMSControl);
        toggleSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mSMSControl = true; }
                else { mSMSControl = false; }
            } // onCheckedChanged

        }); // onCheckedChangeListener

        // Wire up the toggle to enable Email control
        toggleEmail = (ToggleButton) findViewById(R.id.toggle_email);
        toggleEmail.setChecked(mEmailResponse);
        toggleEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mEmailResponse = true; }
                else { mEmailResponse = false; }
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Location reporting
        toggleLocation = (ToggleButton) findViewById(R.id.toggle_location);
        toggleLocation.setChecked(mRemoteLocation);
        toggleLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mRemoteLocation = true; }
                else { mRemoteLocation = false; }
            } // onCheckedChanged

        }); // OnCheckedChangeListener

//        // Register receiver dynamically to access class instance members
//        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(new ReceiveSMS(), filter);

    } // onCreate

    ///////////////////////
    // getSMSpermissions //
    ///////////////////////

    private void getSMSpermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        }
    } // getSMSpermissions

    ////////////////////////////////
    // onRequestPermissionsResult //
    ////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SEND_SMS_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //sendSMSMessage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case REQUEST_SMS_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "SMS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case REQUEST_FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Location permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        } // switch
    } // onRequestPermissionsResult

    ///////////////////////////
    // getLocationPermission //
    ///////////////////////////

    @TargetApi(23)
    public void getLocationPermission() {
        if ( !hasFineLocationPermission() ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_PERMISSION);
        }

    } // getLocationPermission

    ///////////////////////////////
    // hasFineLocationPermission //
    ///////////////////////////////

    /* Check for permissions to access fine location */
    private boolean hasFineLocationPermission () {
        int result = ContextCompat
                .checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result ==  PackageManager.PERMISSION_GRANTED;

    } // hasFineLocationPermission

    ////////////
    // onStop //
    ////////////

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("SMSControl", mSMSControl);
        editor.putBoolean("EmailControl", mEmailResponse);
        editor.putBoolean("RemoteLocation", mRemoteLocation);

        editor.commit();

    }

    /////////////////////
    // showAlertDialog //
    /////////////////////

    public void showAlertDialog() {

        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new DialogFragment();
        dialog.show(getSupportFragmentManager(), "DialogFragment");

    } // showAlertDialog

} // MainActivity

package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.Manifest;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.*;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.DialogFragment;

//////////////////
// MainActivity //
//////////////////

public class MainActivity extends FragmentActivity
        implements KeywordDialog.KeywordDialogListener,
        EmailDialog.EmailDialogListener {

    // Tag to identify this activity in logcat
    private static final String TAG = "RemotePhoneControl";

    // File to save SharedPreferences in
    public static final String PREFS_NAME ="RemotePhoneControl";

    private static final int REQUEST_SMS_PERMISSION = 0;
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    private static final int REQUEST_CALL_PERMISSION = 3;

    // Private members
    boolean mSMSControl;
    boolean mEmailResponse;
    boolean mRemoteLocation;

    String mKeyword;
    String mMyEmail;
    String mPassword;

    // UI Widgets

    Button btnSetKeyword;

    ToggleButton toggleSMS;
    ToggleButton toggleEmail;
    ToggleButton toggleLocation;

    TextView textviewSMS;
    TextView textviewEmail;
    TextView textviewLocation;

    TextView textviewDescription;
    TextView textviewSyntax;
    TextView textviewPermission;

    //////////////
    // onCreate //
    //////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the permissions for SMS & GPS
        getSMSpermissions();
        //getLocationPermission();
        getCallPermission();

        // Load the previous values for user preference...
        // i.e. whether SMS control, email control, location are allowed
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        mSMSControl = settings.getBoolean("SMSControl", false);
        mEmailResponse = settings.getBoolean("EmailControl", false);
        mRemoteLocation = settings.getBoolean("RemoteLocation", false);

        mKeyword = settings.getString("Keyword", "");
        mMyEmail = settings.getString("EmailAddress", "");
        mPassword = settings.getString("Password", "");

        // Wire up the toggle to enable SMS control
        toggleSMS = (ToggleButton) findViewById(R.id.toggle_SMS);
        toggleSMS.setChecked(mSMSControl);
        toggleSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Start the SMS service
                    mSMSControl = true;
                    Intent intent = new Intent(getBaseContext(), SMSListener.class);
                    startService(intent);

                    // Create the KeywordDialog fragment and show it
                    DialogFragment dialog = new KeywordDialog();
                    dialog.show(getFragmentManager(), "KeywordDialogFragment");
                }

                else {
                    mSMSControl = false;
                    Intent intent = new Intent(getBaseContext(), SMSListener.class);
                    stopService(intent);
                }

            } // onCheckedChanged

        }); // onCheckedChangeListener

        // Wire up the toggle to enable Email control
        toggleEmail = (ToggleButton) findViewById(R.id.toggle_email);
        toggleEmail.setChecked(mEmailResponse);
        toggleEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mEmailResponse = true;
                    // Create the dialog fragment and show it
                    DialogFragment dialog = new EmailDialog();
                    dialog.show(getFragmentManager(), "EmailDialogFragment");
                }

                else {
                    mEmailResponse = false;
                }

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

        // Wire up the 'description' text (at the bottom of app)
        textviewDescription = (TextView) findViewById(R.id.textview_description);

        // Wire up the 'syntax' text (at the bottom of app)
        textviewSyntax = (TextView) findViewById(R.id.textview_syntax);

        // Wire up the 'permissions' text (at bottom of app)
        textviewPermission = (TextView) findViewById(R.id.textview_permission);

        // Wire up the "SMS Control" text item
        textviewSMS = (TextView) findViewById(R.id.textview_SMS);
        textviewSMS.setOnClickListener(new View.OnClickListener() {

           public void onClick(View view) {
               textviewDescription.setText(getResources().getString(R.string.description_SMS));
               textviewSyntax.setText(getResources().getString(R.string.syntax_SMS));
               textviewPermission.setText(getResources().getString(R.string.permission_SMS));
           } // onClick

        }); // setOnClickListener

        // Wire up the "Email Response" text item
        textviewEmail = (TextView) findViewById(R.id.textview_email);
        textviewEmail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Email));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Email));
                textviewPermission.setText(getResources().getString(R.string.permission_Email));
            } // onClick

        }); // setOnClickListener


        // Wire up the "Remote Location" text item
        textviewLocation = (TextView) findViewById(R.id.textview_location);
        textviewLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Location));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Location));
                textviewPermission.setText(getResources().getString(R.string.permission_Location));
            } // onClick

        }); // setOnClickListener

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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //sendSMSMessage();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            case REQUEST_SMS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "SMS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            case REQUEST_FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Location permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            case REQUEST_CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Call permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Call permission required, please try again.", Toast.LENGTH_LONG).show();
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

        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        editor.putString("Keyword", mKeyword);
        editor.putString("EmailAddress", mMyEmail);
        editor.putString("Password", mPassword);

        editor.commit();

    } // onStop

    ///////////////////////
    // getCallPermission //
    ///////////////////////

    protected void getCallPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }

    } // getCallPermission

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the KeywordDialogFragment.KeywordDialogListener interface

    ////////////////////////////
    // onKeywordPositiveClick //
    ////////////////////////////

    @Override
    public void onKeywordPositiveClick(DialogFragment dialog) {
        // TODO : Handle case when user hit the 'Set' button
    } // onKeywordPositiveClick

    ////////////////////////////
    // onKeywordNegativeClick //
    ////////////////////////////

    @Override
    public void onKeywordNegativeClick(DialogFragment dialog) {
        // TODO : Handle case when user hit the 'Cancel' button
    } // onKeywordNegativeClick

    ////////////////////////////
    // onEmailPositiveClick //
    ////////////////////////////

    @Override
    public void onEmailPositiveClick(DialogFragment dialog) {
        // TODO : Handle case when user hit the 'Set' button
    } // onEmailPositiveClick

    //////////////////////////
    // onEmailNegativeClick //
    //////////////////////////

    @Override
    public void onEmailNegativeClick(DialogFragment dialog) {
        // TODO : Handle case when user hit the 'Cancel' button
    } // onEmailNegativeClick

} // MainActivity

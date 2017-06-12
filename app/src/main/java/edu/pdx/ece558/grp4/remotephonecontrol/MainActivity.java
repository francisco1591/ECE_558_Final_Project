package edu.pdx.ece558.grp4.remotephonecontrol;

// TODO : Need to terminate service, save preferences, restart service on slider change
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
    private static final int REQUEST_CAMERA_PERMISSION = 4;
    private static final int REQUEST_EXT_STORAGE_PERMISSION = 5;

    // Private members
    boolean mSMSControl;
    boolean mEmailResponse;
    boolean mRemoteLocation;
    boolean mPhoneReponse;
    boolean mPlaySound;
    boolean mTakePicture;

    String mKeyword;
    String mMyEmail;
    String mPassword;

    // UI Widgets

    ToggleButton toggleSMS;
    ToggleButton toggleEmail;
    ToggleButton toggleLocation;
    ToggleButton togglePhone;
    ToggleButton toggleSound;
    ToggleButton togglePicture;

    TextView textviewSMS;
    TextView textviewEmail;
    TextView textviewLocation;
    TextView textviewPhone;
    TextView textviewSound;
    TextView textviewPicture;

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
        getCallPermission();

        // Load the previous values for user preference...
        // i.e. whether SMS control, email control, location are allowed
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        mSMSControl = settings.getBoolean("SMSControl", false);
        mEmailResponse = settings.getBoolean("EmailControl", false);
        mRemoteLocation = settings.getBoolean("RemoteLocation", false);
        mPhoneReponse = settings.getBoolean("PhoneResponse", false);
        mPlaySound = settings.getBoolean("PlaySound", false);
        mTakePicture = settings.getBoolean("TakePicture", false);

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
        toggleEmail = (ToggleButton) findViewById(R.id.toggle_Email);
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
        toggleLocation = (ToggleButton) findViewById(R.id.toggle_Location);
        toggleLocation.setChecked(mRemoteLocation);
        toggleLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mRemoteLocation = true; }
                else { mRemoteLocation = false; }
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Phone response
        togglePhone = (ToggleButton) findViewById(R.id.toggle_Phone);
        togglePhone.setChecked(mPhoneReponse);
        togglePhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mPhoneReponse = true; }
                else { mPhoneReponse = false; }
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Sound to play
        toggleSound = (ToggleButton) findViewById(R.id.toggle_Sound);
        toggleSound.setChecked(mPlaySound);
        toggleSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mPlaySound = true; }
                else { mPlaySound = false; }
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Camera to take picture
        togglePicture = (ToggleButton) findViewById(R.id.toggle_Picture);
        togglePicture.setChecked(mTakePicture);
        togglePicture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { mTakePicture = true; }
                else { mTakePicture = false; }
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
        textviewEmail = (TextView) findViewById(R.id.textview_Email);
        textviewEmail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Email));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Email));
                textviewPermission.setText(getResources().getString(R.string.permission_Email));
            } // onClick

        }); // setOnClickListener


        // Wire up the "Remote Location" text item
        textviewLocation = (TextView) findViewById(R.id.textview_Location);
        textviewLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Location));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Location));
                textviewPermission.setText(getResources().getString(R.string.permission_Location));
            } // onClick

        }); // setOnClickListener

        // Wire up the "Phone Response" text item
        textviewPhone = (TextView) findViewById(R.id.textview_Phone);
        textviewPhone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Phone));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Phone));
                textviewPermission.setText(getResources().getString(R.string.permission_Phone));
            } // onClick

        }); // setOnClickListener

        // Wire up the "Play Sound" text item
        textviewSound = (TextView) findViewById(R.id.textview_Sound);
        textviewSound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Sound));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Sound));
                textviewPermission.setText(getResources().getString(R.string.permission_Sound));
            } // onClick

        }); // setOnClickListener

        // Wire up the "Take Picture" text item
        textviewPicture = (TextView) findViewById(R.id.textview_Picture);
        textviewPicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                textviewDescription.setText(getResources().getString(R.string.description_Picture));
                textviewSyntax.setText(getResources().getString(R.string.syntax_Picture));
                textviewPermission.setText(getResources().getString(R.string.permission_Picture));
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

            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"Camera permission granted.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Camera permission required, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            case REQUEST_EXT_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"External storage permission granted.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "External storage permission required, please try again.", Toast.LENGTH_LONG).show();
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
        editor.putBoolean("PhoneResponse", mPhoneReponse);
        editor.putBoolean("PlaySound", mPlaySound);
        editor.putBoolean("TakePicture", mTakePicture);

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

    /////////////////////////
    // getCameraPermission //
    /////////////////////////

    protected void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
        }
    } // getCameraPermission

    /////////////////////////////
    // getExtStoragePermission //
    /////////////////////////////

    protected void getExtStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXT_STORAGE_PERMISSION);
        }
    } // getExtStoragePermission

} // MainActivity

package edu.pdx.ece558.grp4.remotephonecontrol;

// TODO : Color the toggles more clearly between on & off
// TODO : Turn the permissions on & off within the toggles
// TODO : Fix bug with infinitely restarting SMSListener

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
    private static final String TAG = "SpyOnMe";

    // Constant IDs for passing arguments to DescriptionDialog
    public static final int ID_SMS = 0;
    public static final int ID_EMAIL = 1;
    public static final int ID_LOCATION = 2;
    public static final int ID_PHONE = 3;
    public static final int ID_SOUND = 4;
    public static final int ID_PICTURE = 5;

    // File to save SharedPreferences in
    public static final String PREFS_NAME ="RemotePhoneControl";

    // Constant IDs for determining permissions
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
    boolean mPhoneResponse;
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

    //////////////
    // onCreate //
    //////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the permissions for all services
        getSMSpermissions();
        getLocationPermission();
        getCallPermission();
        getCameraPermission();
        getExtStoragePermission();

        // Load the previous values for user preference...
        // i.e. whether SMS control, email control, location are allowed
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        mSMSControl = settings.getBoolean("SMSControl", false);
        mEmailResponse = settings.getBoolean("EmailControl", false);
        mRemoteLocation = settings.getBoolean("RemoteLocation", false);
        mPhoneResponse = settings.getBoolean("PhoneResponse", false);
        mPlaySound = settings.getBoolean("PlaySound", false);
        mTakePicture = settings.getBoolean("TakePicture", false);

        mKeyword = settings.getString("Keyword", "");
        mMyEmail = settings.getString("EmailAddress", "");
        mPassword = settings.getString("Password", "");

        // Wire up the toggle to enable Location reporting
        toggleLocation = (ToggleButton) findViewById(R.id.toggle_Location);
        toggleLocation.setChecked(mRemoteLocation);
        toggleLocation.setEnabled(mSMSControl);
        toggleLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mRemoteLocation = toggleLocation.isChecked();
                refreshSMSListener();
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Phone response
        togglePhone = (ToggleButton) findViewById(R.id.toggle_Phone);
        togglePhone.setChecked(mPhoneResponse);
        togglePhone.setEnabled(mSMSControl);
        togglePhone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mPhoneResponse = togglePhone.isChecked();
                refreshSMSListener();
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Sound to play
        toggleSound = (ToggleButton) findViewById(R.id.toggle_Sound);
        toggleSound.setChecked(mPlaySound);
        toggleSound.setEnabled(mSMSControl);
        toggleSound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mPlaySound = toggleSound.isChecked();
                refreshSMSListener();
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Camera to take picture
        togglePicture = (ToggleButton) findViewById(R.id.toggle_Picture);
        togglePicture.setChecked(mTakePicture);
        togglePicture.setEnabled(mEmailResponse);
        togglePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mTakePicture = togglePicture.isChecked();
                refreshSMSListener();
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable Email control
        toggleEmail = (ToggleButton) findViewById(R.id.toggle_Email);
        toggleEmail.setChecked(mEmailResponse);
        toggleEmail.setEnabled(mSMSControl);
        toggleEmail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mEmailResponse = toggleEmail.isChecked();

                if (mEmailResponse) {
                    // enable the picture toggle again
                    if(mSMSControl) {togglePicture.setEnabled(true);}

                    // Create the dialog fragment and show it
                    DialogFragment dialog = new EmailDialog();
                    dialog.show(getFragmentManager(), "EmailDialogFragment");
                }

                else {
                    togglePicture.setChecked(false);
                    togglePicture.setEnabled(false);
                    mTakePicture = false;
                }

                refreshSMSListener();
            } // onCheckedChanged

        }); // OnCheckedChangeListener

        // Wire up the toggle to enable SMS control
        toggleSMS = (ToggleButton) findViewById(R.id.toggle_SMS);
        toggleSMS.setChecked(mSMSControl);
        toggleSMS.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mSMSControl = toggleSMS.isChecked();

                if (mSMSControl) {
                    // enable the toggle controls again
                    toggleEmail.setEnabled(true);
                    toggleLocation.setEnabled(true);
                    togglePhone.setEnabled(true);
                    toggleSound.setEnabled(true);
                    if (mTakePicture) {togglePicture.setEnabled(true);};

                    // Create the KeywordDialog fragment and show it
                    DialogFragment dialog = new KeywordDialog();
                    dialog.show(getFragmentManager(), "KeywordDialogFragment");
                }

                else {
                    // turn off everything, since they all depend on SMS control
                    toggleEmail.setChecked(false);
                    toggleEmail.setEnabled(false);
                    mEmailResponse = false;
                    toggleLocation.setChecked(false);
                    toggleLocation.setEnabled(false);
                    mRemoteLocation = false;
                    togglePhone.setChecked(false);
                    togglePhone.setEnabled(false);
                    mPhoneResponse = false;
                    toggleSound.setChecked(false);
                    toggleSound.setEnabled(false);
                    mPlaySound = false;
                    togglePicture.setChecked(false);
                    togglePicture.setEnabled(false);
                    mTakePicture = false;
                }
                refreshSMSListener();
            } // onCheckedChanged

        }); // onCheckedChangeListener

        // Wire up the "SMS Control" text item
        textviewSMS = (TextView) findViewById(R.id.textview_SMS);
        textviewSMS.setOnClickListener(new View.OnClickListener() {

           public void onClick(View view) {
               // Start the DescriptionDialog fragment w/ 'SMS' as argument
               DialogFragment dialog = newDescriptionDialog(ID_SMS);
               dialog.show(getFragmentManager(), "DescriptionDialog");
           } // onClick

        }); // setOnClickListener

        // Wire up the "Email Response" text item
        textviewEmail = (TextView) findViewById(R.id.textview_Email);
        textviewEmail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start the DescriptionDialog fragment w/ 'Email' as argument
                DialogFragment dialog = newDescriptionDialog(ID_EMAIL);
                dialog.show(getFragmentManager(), "DescriptionDialog");
            } // onClick

        }); // setOnClickListener


        // Wire up the "Remote Location" text item
        textviewLocation = (TextView) findViewById(R.id.textview_Location);
        textviewLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start the DescriptionDialog fragment w/ 'Location' as argument
                DialogFragment dialog = newDescriptionDialog(ID_LOCATION);
                dialog.show(getFragmentManager(), "DescriptionDialog");
            } // onClick

        }); // setOnClickListener

        // Wire up the "Phone Response" text item
        textviewPhone = (TextView) findViewById(R.id.textview_Phone);
        textviewPhone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start the DescriptionDialog fragment w/ 'Phone' as argument
                DialogFragment dialog = newDescriptionDialog(ID_PHONE);
                dialog.show(getFragmentManager(), "DescriptionDialog");
            } // onClick

        }); // setOnClickListener

        // Wire up the "Play Sound" text item
        textviewSound = (TextView) findViewById(R.id.textview_Sound);
        textviewSound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start the DescriptionDialog fragment w/ 'Sound' as argument
                DialogFragment dialog = newDescriptionDialog(ID_SOUND);
                dialog.show(getFragmentManager(), "DescriptionDialog");
            } // onClick

        }); // setOnClickListener

        // Wire up the "Take Picture" text item
        textviewPicture = (TextView) findViewById(R.id.textview_Picture);
        textviewPicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start the DescriptionDialog fragment w/ 'Picture' as argument
                DialogFragment dialog = newDescriptionDialog(ID_PICTURE);
                dialog.show(getFragmentManager(), "DescriptionDialog");
            } // onClick

        }); // setOnClickListener

    } // onCreate

    ////////////
    // onStop //
    ////////////

    @Override
    protected void onStop() {

        super.onStop();
        saveSettings();

    } // onStop

    //////////////////
    // saveSettings //
    //////////////////

    protected void saveSettings() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("SMSControl", mSMSControl);
        editor.putBoolean("EmailControl", mEmailResponse);
        editor.putBoolean("RemoteLocation", mRemoteLocation);
        editor.putBoolean("PhoneResponse", mPhoneResponse);
        editor.putBoolean("PlaySound", mPlaySound);
        editor.putBoolean("TakePicture", mTakePicture);

        editor.putString("Keyword", mKeyword);
        editor.putString("EmailAddress", mMyEmail);
        editor.putString("Password", mPassword);

        editor.commit();

    } // saveSettings

    ////////////////////////
    // refreshSMSListener //
    ////////////////////////
    private void refreshSMSListener() {

        Intent intent = new Intent(getBaseContext(), SMSListener.class);

        stopService(intent);
        saveSettings();
        startService(intent);

    } // refreshSMSListener

    ////////////////////////////
    // onKeywordPositiveClick //
    ////////////////////////////

    @Override
    public void onKeywordPositiveClick(String keyword) {
        mKeyword = keyword;
        refreshSMSListener();
    } // onKeywordPositiveClick

    ////////////////////////////
    // onEmailPositiveClick //
    ////////////////////////////

    @Override
    public void onEmailPositiveClick(String email, String password) {
        mMyEmail = email;
        mPassword = password;
        refreshSMSListener();
    } // onEmailPositiveClick

    ///////////////////////
    // getSMSpermissions //
    ///////////////////////

    private void getSMSpermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    } // getSMSpermissions

    ///////////////////////////
    // getLocationPermission //
    ///////////////////////////

    @TargetApi(23)
    public void getLocationPermission() {

        if ( !hasFineLocationPermission() ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
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

    ///////////////////////
    // getCallPermission //
    ///////////////////////

    protected void getCallPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }

    } // getCallPermission

    /////////////////////////
    // getCameraPermission //
    /////////////////////////

    protected void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
        }
    } // getCameraPermission

    /////////////////////////////
    // getExtStoragePermission //
    /////////////////////////////

    protected void getExtStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXT_STORAGE_PERMISSION);
        }
    } // getExtStoragePermission

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
                    Toast.makeText(getApplicationContext(), "SMS permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

            case REQUEST_SMS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "SMS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

            case REQUEST_FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Location permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

            case REQUEST_CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Call permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Call permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"Camera permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

            case REQUEST_EXT_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"External storage permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "External storage permission required, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } break;

        } // switch
    } // onRequestPermissionsResult

    //////////////////////////
    // newDescriptionDialog //
    //////////////////////////

    public static DescriptionDialog newDescriptionDialog(int textID) {

        Bundle args = new Bundle();
        args.putInt(DescriptionDialog.EXTRA_TEXTVIEW_ID, textID);
        DescriptionDialog dialog = new DescriptionDialog();
        dialog.setArguments(args);
        return dialog;

    } // newDescriptionDialog

} // MainActivity

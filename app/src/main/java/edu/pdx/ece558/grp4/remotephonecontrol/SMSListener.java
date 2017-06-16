package edu.pdx.ece558.grp4.remotephonecontrol;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created by Francisco on 6/9/2017.
 */

public class SMSListener extends Service {
    private final static String TAG = "SMSListener";
    // File to save SharedPreferences in
    public static final String PREFS_NAME ="RemotePhoneControl";
    //String phoneNo;
    String mMessage;
    String mPhoneNo;
    String mSender;
    String mGateway;
    private boolean go;
    private ReceiveSMS receiveSMS; //BroadcastReceiver
    private String mSubject;
    // Preferences
    boolean mSMSControl;
    boolean mEmailResponse;
    boolean mRemoteLocation;
    boolean mPhoneResponse;
    boolean mPlaySound;
    boolean mTakePicture;
    String mKeyword;
    String mPassword;
    String mMyEmail;


    private static final Hashtable<String,String> MMSgateway = new Hashtable<String,String>() {{
        put("at","@mms.att.net"); // at&t
        put("bo","@myboostmobile.com"); // boost
        put("cr", "@mms.cricketwireless.net"); // cricket
        put("sp", "@pm.sprint.com"); // sprint
        put("tm", "@tmomail.net"); // t-mobile
        put("us","@mms.uscc.net"); // us cellular
        put("ve","@vzwpix.com"); // verizon wireless
        put("vi","@vmpix.com"); // virgin mobile
    }};


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private static final String description_EmailResponse = "Enabling this option allows you" +
            "to text your phone from an email account via an SMS Gateway," +
            "sending an email to the address 1234567890@carrierDomain.com, where the numbers to" +
            "the left of @ are your 10-digit phone number and the domain is specific to your " +
            "phone carrier. Your phone will then automatically reply to the sender email address." +
            " You will need to provide login credentials to a Gmail account, " +
            "which will be used to send the response email on behalf of your phone." +
            "IMPORTANT: You also need to authorize external access to your Gmail account" +
            " by enabling “less secure apps” in settings:" +
            " https://www.google.com/settings/security/lesssecureapps " +
            "before this feature will work. You can create a dummy gmail account if you don't " +
            "feel comfortable authorizing external access to your personal account.";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        mSMSControl = settings.getBoolean("SMSControl", false);
        mEmailResponse = settings.getBoolean("EmailControl", false);
        mRemoteLocation = settings.getBoolean("RemoteLocation", false);
        mPhoneResponse = settings.getBoolean("PhoneResponse", false);
        mPlaySound = settings.getBoolean("PlaySound", false);
        mTakePicture = settings.getBoolean("TakePicture", false);

        mKeyword = settings.getString("Keyword", "");
        mMyEmail = settings.getString("EmailAddress", "");
        mPassword = settings.getString("Password", "");

        // Let it continue running until it is stopped.
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        // Register receiver dynamically to access class instance members
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiveSMS = new ReceiveSMS();
        registerReceiver( receiveSMS, filter);
        go = true;
        Thread thread = new Thread() {
            public void run () {
                long count = 0;
                String s;
                while (go){
                    try {
                        Thread.sleep(1000);
                        count++;
                        s = "Service running for "+count+" seconds.";
                        Log.d(TAG, s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        go = false;
        unregisterReceiver(receiveSMS);
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    // Inner class for Broadcast Receiver
    public class ReceiveSMS extends BroadcastReceiver {
        // Get the object of SmsManager
        SmsManager sms = SmsManager.getDefault();
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        mSender = currentMessage.getDisplayOriginatingAddress();
                        mMessage = currentMessage.getDisplayMessageBody();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context,
                                "senderNum: " + mSender + ", message: " + mMessage, duration);
                        toast.show();
                        String words[] = mMessage.split("\n| |\t"); // delimiters \n,\t and space
                        int j = Arrays.asList(words).indexOf(mKeyword);
                        if (j >= 0) {
                            handleRequest(words, j);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception smsReceiver" + e);
            }
        }
    }

    public void handleRequest (String words [] , int j){
        // force lowercase in case
        for (int i=0; i < words.length; i++){
            words[i] = words[i].toLowerCase();
        }
        String command = words[j+1];
        switch (command){
            case "find":
                // check if feature enabled, and email enabled in case of email sender
                if (!mRemoteLocation || (mSender.indexOf('@') > 0 && !mEmailResponse) )
                    return;
                mSubject = "Response to location request";

                getLocation();
                break;
            case "pic":  {
                // check if feature enabled
                if (!mTakePicture)
                    return;
                mSubject = "Response to picture request";
                boolean frontCamera = true; // default
                boolean flash = false; // default
                if (mSender.indexOf('@') < 0) {// sender is phone, must have carrier parameter
                    if (words.length <= j + 2) // Didn't provide carrier parameter
                        return;
                    else {
                        if( !mSender.matches("\\d{10}") ) // get only last 10 digits if +1 included
                            mSender = mSender.substring(mSender.length()-10);
                        mGateway = mSender + MMSgateway.get(words[j+2]);
                    }
                    if (words.length > j + 3) {
                        if (words[j + 3].equals("back")) { //checks for back camera option
                            frontCamera = false;
                            if (words.length > j + 4) { // flash available on back camera only (usually)
                                if (words[j + 4].equals("flash"))
                                    flash = true;
                            }
                        }
                    }

                } else { // sender is email
                    // check if feature enabled
                    if (!mEmailResponse)
                        return;
                    if (words.length > j + 2) {
                        if (words[j + 2].equals("back")) { // checks for back camera option
                            frontCamera = false;
                            if (words.length > j + 3) {
                                if (words[j + 3].equals("flash")) // if flash on, only on back cam
                                    flash = true;
                            }
                        }
                    }

                }
                Picture pic = new Picture(SMSListener.this, frontCamera);
                pic.setFlash(flash);
                pic.takePic();
                break;
            }
            case "call":
                // check if feature enabled
                if (!mPhoneResponse)
                    return;
                if (mSender.indexOf('@') < 0) // sender is phone
                    makeCall(mSender);
                    // else sender is email address, validate phone num
                else if(words.length > j+2 && words[j+2].matches("\\d{10}"))
                    makeCall(words[j+2]);
                break;
            case "alert":  {
                // check if feature enabled
                if (!mPlaySound)
                    return;
                int duration = 5; //default, seconds
                // check if duration option included
                if (words.length > j + 2 && words[j+2].matches("\\d+")) {
                    duration = Integer.parseInt(words[j + 2]); // play for duration seconds
                    if (duration > 25 ) // don't want to hear alarm forever
                        duration = 25;
                }
                soundAlert(duration);
                break;
            }
            default:
                return;
        }
    }

    public void soundAlert (final int duration) {
        try {
            Thread delay = new Thread () {
                public void run (){
                    AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    // save ringer mode so we can restore it in the end
                    int mode = am.getRingerMode();
                    int volume = am.getStreamVolume(AudioManager.STREAM_RING);
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    am.setStreamVolume (AudioManager.STREAM_RING,am.getStreamMaxVolume(AudioManager.STREAM_RING),0);
                    Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    Ringtone alert = RingtoneManager.getRingtone(getApplicationContext(), alarm);
                    alert.play();
                    try {
                        Thread.sleep(duration*1000);
                        alert.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Restore ringer mode & volume
                    am.setRingerMode(mode);
                    am.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
                }
            };
            delay.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeCall (String phoneNo) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNo ));
            startActivity(callIntent);
        } catch (SecurityException sex) {
            Log.e(TAG, "Error making phone call: " + sex.getMessage());
        }
    }
    // Takes a message and an attachment
    public void replyToSender (String msg, String filename) {
        if (mSender.indexOf('@') < 0) { // sender is phone
            if (filename == null) {// no attachment
                sendSMSMessage(msg);
            } else {
                mSender = mGateway;
                sendEmail(msg,filename);
            }
        } else { // sender is email address
            sendEmail(msg, filename);
        }
    }

    public void sendEmail(final String msg, final String filename) {
        Thread mailSender = new Thread() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender(mMyEmail,
                            mPassword);
                    sender.sendMail(mSubject, msg,
                            mMyEmail, mSender, filename);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };
        mailSender.start();
        Log.d(TAG, "Email sent.");
    }

    protected void sendSMSMessage(String msg) {
            String mesg = mSubject + "\n" + msg;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mSender, null, mesg, null, null);
            Log.d(TAG, "SMS sent.");
    }

    private void getLocation() {
        final Locate loc = new Locate(this,5e3,15);
        loc.getLocationUpdates();
        // In case we don't get a fresh location, use last known location
        Thread checkLoc = new Thread () {
            public void run () {
                try {
                    Thread.sleep(15000);
                    if (!loc.mFoundLocation) { // didn't find location, return last known
                        loc.getLastKnownLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        checkLoc.start();
    }
}

package edu.pdx.ece558.grp4.remotephonecontrol;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Francisco on 6/9/2017.
 */

public class SMSListener extends Service {
    private final static String TAG = "SMSListener";
    String phoneNo;
    String message;
    private boolean go;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    // Inner class for Broadcast Receiver
    public class ReceiveSMS extends BroadcastReceiver {

        // Get the object of SmsManager
        SmsManager sms = SmsManager.getDefault();

        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {
                // test ccomment
                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String senderID = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();

                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "senderNum: " + senderID + ", message: " + message, duration);
                        toast.show();

                        // Reply
                        //sendSMSMessage();
                        // SMTP server information
                        String host = "smtp.gmail.com";
                        String port = "465";
                        String mailFrom = "YOUR_EMAIL";
                        String password = "**REMOVED**";

                        // outgoing message information
                        String mailTo = "fjl@pdx.edu";
                        String subject = "Semper cemper dassus antes";
                        sendEmail();;
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception smsReceiver" + e);
            }
        }
    }

    private void sendEmail() {
        Thread mailSender = new Thread() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender("francisco1591@gmail.com",
                            "iha.sari");
                    sender.sendMail("Ya se cocio ", "A huihui!",
                            "francisco1591@gmail.com", "fjl@pdx.edu");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };
        mailSender.start();
    }
    protected void sendSMSMessage() {
        phoneNo = "8472196443";
        message = "Quioboles que compita, a donde vas que más valgas";
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.SEND_SMS},
//                    REQUEST_SEND_SMS_PERMISSION);
//        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
//        }
    }
}

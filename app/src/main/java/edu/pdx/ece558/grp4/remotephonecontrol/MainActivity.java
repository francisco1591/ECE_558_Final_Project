package edu.pdx.ece558.grp4.remotephonecontrol;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RemotePhoneControl";
    private static final int REQUEST_SMS_PERMISSION = 0;
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    Button btnStart;
    Button btnStop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSMSpermissions();
        getLocationPermission();

        //TODO Add user interface to configure app
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SMSListener.class);
                startService(intent);
            }
        });

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SMSListener.class);
                stopService(intent);
            }
        });

//        // Register receiver dynamically to access class instance members
//        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(new ReceiveSMS(), filter);
    }

    private void getSMSpermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        }
    }

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
        }
    }

    @TargetApi(23)
    public void getLocationPermission() {
        if ( !hasFineLocationPermission() ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_PERMISSION);
        }

    }

    /* Check for permissions to access fine location */
    private boolean hasFineLocationPermission () {
        int result = ContextCompat
                .checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result ==  PackageManager.PERMISSION_GRANTED;
    }

}

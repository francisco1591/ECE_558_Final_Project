package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.app.DialogFragment;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.widget.*;

public class HomeActivity extends FragmentActivity
        implements KeywordDialog.KeywordDialogListener {

    // Tag to identify this activity in logcat
    private static final String TAG = "SpyOnMe.HomeActivity";

    // File to save SharedPreferences in
    public static final String PREFS_NAME ="RemotePhoneControl";

    // Private members
    String mKeyword;

    // UI Widgets
    Button button_keyword;

    //////////////
    // onCreate //
    //////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // restore the previous keyword (if it exists)
        // otherwise, set keyword to empty
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mKeyword = settings.getString("Keyword", "");

        button_keyword = (Button) findViewById(R.id.button_keyword);
        button_keyword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (mKeyword.isEmpty()) {
                    // Start the MainActivity if keyword is blank
                    // i.e. if user is starting service for first time
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(i);
                }

                else {
                    // Create the KeywordDialog fragment and show it
                    android.app.DialogFragment dialog = new KeywordDialog();
                    dialog.show(getFragmentManager(), "KeywordDialogFragment");
                }

            } // onClick
        }); // setOnClickListener

    } // onCreate

    //////////////
    // onResume //
    //////////////

    @Override
    public void onResume(){
        super.onResume();
        // restore the previous keyword (if it exists)
        // otherwise, set keyword to empty
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mKeyword = settings.getString("Keyword", "");
    }

    ////////////////////////////
    // onKeywordPositiveClick //
    ////////////////////////////

    @Override
    public void onKeywordPositiveClick(String keyword) {
        String userResponse = keyword;

        if (userResponse.equals(mKeyword)) {
            // start the MainActivity if keyword validates correctly
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(i);

        }

        else {
            Toast.makeText(getApplicationContext(),"Incorrect keyword!", Toast.LENGTH_SHORT).show();
        }
    } // onKeywordPositiveClick

} // HomeActivity

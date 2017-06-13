package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.app.*;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;

public class DescriptionDialog extends DialogFragment {

    // data being passed from MainActivity --> DescriptionDialog
    public static final String EXTRA_TEXTVIEW_ID = "edu.pdx.ece558.grp4.remotephonecontrol.textview_id";

    // UI Widgets
    TextView textviewDescription;
    TextView textviewSyntax;
    TextView textviewPermission;

    // Private members
    String strTitle;
    String strDescription;
    String strSyntax;
    String strPermission;

    ////////////////////
    // onCreateDialog //
    ////////////////////

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // update all the strings needed for dialog box
        Bundle args = getArguments();
        int textview_id = args.getInt(EXTRA_TEXTVIEW_ID, 0);
        updateDialogStrings(textview_id);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.dialog_description, null);

        // Create title for the dialog window
        builder.setView(v).setTitle(strTitle);

        // Wire up the 'description' text (at the bottom of app)
        textviewDescription = (TextView) v.findViewById(R.id.textview_description);
        textviewDescription.setText(strDescription);

        // Wire up the 'syntax' text (at the bottom of app)
        textviewSyntax = (TextView) v.findViewById(R.id.textview_syntax);
        textviewSyntax.setText(strSyntax);

        // Wire up the 'permissions' text (at bottom of app)
        textviewPermission = (TextView) v.findViewById(R.id.textview_permission);
        textviewPermission.setText(strPermission);

        // Get the AlertDialog from create()
        return builder.create();

    } // onCreateDialog

    /////////////////////////
    // updateDialogStrings //
    /////////////////////////

    protected void updateDialogStrings(int textview_id) {

        switch (textview_id) {

            case (MainActivity.ID_SMS) :
                strTitle = getResources().getString(R.string.textview_SMS);
                strDescription = getResources().getString(R.string.description_SMS);
                strSyntax = getResources().getString(R.string.syntax_SMS);
                strPermission = getResources().getString(R.string.permission_SMS);
                break;

            case (MainActivity.ID_EMAIL) :
                strTitle = getResources().getString(R.string.textview_Email);
                strDescription = getResources().getString(R.string.description_Email);
                //strSyntax = getResources().getString(R.string.syntax_Email);
                strPermission = getResources().getString(R.string.permission_Email);
                break;

            case (MainActivity.ID_LOCATION) :
                strTitle = getResources().getString(R.string.textview_Location);
                strDescription = getResources().getString(R.string.description_Location);
                strSyntax = getResources().getString(R.string.syntax_Location);
                strPermission = getResources().getString(R.string.permission_Location);
                break;

            case (MainActivity.ID_PHONE) :
                strTitle = getResources().getString(R.string.textview_Phone);
                strDescription = getResources().getString(R.string.description_Phone);
                strSyntax = getResources().getString(R.string.syntax_Phone);
                strPermission = getResources().getString(R.string.permission_Phone);
                break;

            case (MainActivity.ID_SOUND) :
                strTitle = getResources().getString(R.string.textview_Sound);
                strDescription = getResources().getString(R.string.description_Sound);
                strSyntax = getResources().getString(R.string.syntax_Sound);
                strPermission = getResources().getString(R.string.permission_Sound);
                break;

            case (MainActivity.ID_PICTURE) :
                strTitle = getResources().getString(R.string.textview_Picture);
                strDescription = getResources().getString(R.string.description_Picture);
                strSyntax = getResources().getString(R.string.syntax_Picture);
                strPermission = getResources().getString(R.string.permission_Picture);
                break;

        } // switch
    } // updateDialogStrings
} // DialogFragment

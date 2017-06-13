package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.app.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

public class EmailDialog extends DialogFragment {

    ////////////////////
    // onCreateDialog //
    ////////////////////

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_email, null))

                // Create title for the dialog window
                .setTitle(R.string.title_email_dialog)

                // Add 'Set' action button
                .setPositiveButton(R.string.actionbtn_set, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onEmailPositiveClick(EmailDialog.this);
                    } // onClick
                }) // setPositiveButton

                // Add the 'Cancel' action button
                .setNegativeButton(R.string.actionbtn_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onEmailNegativeClick(EmailDialog.this);
                    } // onClick
                }); // setNegativeButton

        // Get the AlertDialog from create()
        return builder.create();

    } // onCreateDialog

    ///////////////////////////
    // EmailDialogListener //
    ///////////////////////////

    // The activity that instantiates this dialog fragment
    // must implement this interface in order to receive callbacks.
    // Each method passes the DialogFragment in case host needs to query it.

    public interface EmailDialogListener {
        public void onEmailPositiveClick(DialogFragment dialog);
        public void onEmailNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    EmailDialogListener mListener;

    //////////////
    // onAttach //
    //////////////

    // Override the Fragment.onAttach() method to instantiate the EmailDialogListener

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the EmailDialogListener so we can send events to host
            mListener = (EmailDialogListener) activity;
        }

        // The activity doesn't implement the interface... throw exception
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement EmailDialogListener");
        }

    } // onAttach

} // DialogFragment

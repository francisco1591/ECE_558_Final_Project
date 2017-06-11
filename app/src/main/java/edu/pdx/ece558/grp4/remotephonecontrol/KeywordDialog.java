package edu.pdx.ece558.grp4.remotephonecontrol;

/////////////////////
// Android Imports //
/////////////////////

import android.app.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;


public class KeywordDialog extends DialogFragment {

    // UI Widgets

    EditText mEditTextKeyword;

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
        builder.setView(inflater.inflate(R.layout.dialog_keyword, null))

            // Create title for the dialog window
            .setTitle(R.string.title_keyword_dialog)

            // Add 'Set' action button
            .setPositiveButton(R.string.actionbtn_set, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onKeywordPositiveClick(KeywordDialog.this);
                } // onClick
            }) // setPositiveButton

            // Add the 'Cancel' action button
                .setNegativeButton(R.string.actionbtn_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onKeywordNegativeClick(KeywordDialog.this);
                    } // onClick
                }); // setNegativeButton

        // Get the AlertDialog from create()
        return builder.create();

    } // onCreateDialog

    ///////////////////////////
    // KeywordDialogListener //
    ///////////////////////////

    // The activity that instantiates this dialog fragment
    // must implement this interface in order to receive callbacks.
    // Each method passes the DialogFragment in case host needs to query it.

    public interface KeywordDialogListener {
        public void onKeywordPositiveClick(DialogFragment dialog);
        public void onKeywordNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    KeywordDialogListener mListener;

    //////////////
    // onAttach //
    //////////////

    // Override the Fragment.onAttach() method to instantiate the KeywordDialogListener

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the KeywordDialogListener so we can send events to host
            mListener = (KeywordDialogListener) activity;
        }

        // The activity doesn't implement the interface... throw exception
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement KeywordDialogListener");
        }

    } // onAttach

} // DialogFragment

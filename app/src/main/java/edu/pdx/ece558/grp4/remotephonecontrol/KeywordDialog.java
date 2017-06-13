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
import android.view.View;

public class KeywordDialog extends DialogFragment {

    // UI Widgets
    EditText edittext_keyword;

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
        View v = inflater.inflate(R.layout.dialog_keyword, null);

        // Wire up the EditText for keyword
        edittext_keyword = (EditText) v.findViewById(R.id.edittext_keyword);

        // Create title for the dialog window
        builder.setView(v).setTitle(R.string.title_keyword_dialog);

        // Add 'Set' action button
        builder.setPositiveButton(R.string.actionbtn_set, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strKeyword = edittext_keyword.getText().toString();
                mListener.onKeywordPositiveClick(strKeyword);
            } // onClick
        }); // setPositiveButton

        // Add the 'Cancel' action button
            builder.setNegativeButton(R.string.actionbtn_cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Doesn't need to do anything for cancel
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
        public void onKeywordPositiveClick(String keyword);
    } // KeywordDialogListener

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

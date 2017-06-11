package edu.pdx.ece558.grp4.remotephonecontrol;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

public class DialogFragment extends Fragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // instantiate an AlerDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_alert, null));

        // Get the AlertDialog from create()
        return builder.create();
    }
} // DialogFragment

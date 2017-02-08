package com.ggccnu.tinynote.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by lishaowei on 16/1/10.
 */
public abstract class MyDialogFragment extends DialogFragment {

    public MyDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("要删掉吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialogPositiveButtonClicked();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialogNegativeButtonClicked();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public abstract void dialogPositiveButtonClicked();
    public abstract void dialogNegativeButtonClicked();
}

package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;

/**
 * Click listener for the dialog boxes that will dismiss the dialog
 * when the button it is attached to is pressed.
 *
 * @version 1
 */
public class DismissClickListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}

package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;

import com.abbasandfriends.injurymonitoringsystem.MainAppActivity;

/**
 * Click listener for the dialog boxes that will dismiss the dialog
 * when the button it is attached to is pressed.
 *
 * @version 1
 */
public class DismissClickListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        MainAppActivity.dialogFlag = false;
        dialog.dismiss();
    }
}

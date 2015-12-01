package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;

/**
 * Click listener that will make a request for emergency response by sending
 * an Alarm object to the database.
 *
 * @version 1
 */
public class RequestListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO send request to database for emergency response
        dialog.dismiss();
    }
}

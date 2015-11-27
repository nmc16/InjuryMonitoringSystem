package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;

/**
 * Created by Nic on 11/26/2015.
 */
public class RequestListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO send request to database for emergency response
        dialog.dismiss();
    }
}

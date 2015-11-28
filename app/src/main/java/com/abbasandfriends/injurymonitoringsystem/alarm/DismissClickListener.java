package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.content.DialogInterface;

/**
 * Created by Nic on 11/26/2015.
 */
public class DismissClickListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}

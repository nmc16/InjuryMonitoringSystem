package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.abbasandfriends.injurymonitoringsystem.R;

/**
 * Created by Nic on 11/26/2015.
 */
public class AlarmDialog {
    private AlertDialog.Builder builder;

    public AlarmDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity);
    }

    public AlertDialog create() {
        builder.setTitle(R.string.alarm_dialog_title);
        builder.setMessage("Alarm info: ");

        builder.setPositiveButton(R.string.alarm_dialog_request, new RequestListener());
        builder.setNegativeButton(R.string.alarm_dialog_dismiss, new DismissClickListener());

        return builder.create();
    }

}

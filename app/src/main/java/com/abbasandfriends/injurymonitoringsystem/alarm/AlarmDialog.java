package com.abbasandfriends.injurymonitoringsystem.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.abbasandfriends.injurymonitoringsystem.R;

import sendable.alarm.Alarm;
import sendable.alarm.DataCause;
import sendable.alarm.PlayerCause;

/**
 * Created by Nic on 11/26/2015.
 */
public class AlarmDialog {
    private AlertDialog.Builder builder;

    public AlarmDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity);
    }

    public AlertDialog create(Alarm alarm) {
        builder.setTitle(R.string.alarm_dialog_title);

        StringBuilder sb = new StringBuilder("Cause: ");
        if (alarm.getCause() instanceof PlayerCause) {
           sb.append("Player ").append(((PlayerCause) alarm.getCause()).getPlayerID())
                   .append(" pressed emergency button!");
        } else if (alarm.getCause() instanceof DataCause) {
            sb.append("Data crossed ").append(((DataCause) alarm.getCause()).getThreshold())
                    .append(" threshold!");
        } else {
            sb.append("Unkown alarm cause");
        }

        builder.setMessage(sb);

        builder.setPositiveButton(R.string.alarm_dialog_request, new RequestListener());
        builder.setNegativeButton(R.string.alarm_dialog_dismiss, new DismissClickListener());

        return builder.create();
    }

}

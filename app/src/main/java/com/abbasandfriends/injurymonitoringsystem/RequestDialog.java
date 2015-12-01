package com.abbasandfriends.injurymonitoringsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import sendable.Sendable;

public class RequestDialog {
    private AlertDialog.Builder builder;
    private Activity activity;

    public RequestDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity);
        this.activity = activity;
    }

    public AlertDialog create() {
        builder.setTitle(R.string.request_dialog_title);
        builder.setMessage(R.string.request_dialog_msg);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText startTime = new EditText(activity);
        startTime.setHint("Start time...");
        layout.addView(startTime);

        final EditText endTime = new EditText(activity);
        endTime.setHint("End time...");
        layout.addView(endTime);

        final EditText player = new EditText(activity);
        player.setHint("Player ID...");
        layout.addView(player);

        final ListView listView = new ListView(activity);
        final CharSequence items[] = {"Position", "Acceleration", "Alarm"};

        builder.setView(layout);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(startTime.getText().toString());
            }
        });

        return builder.create();
    }
}

package com.abbasandfriends.injurymonitoringsystem.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abbasandfriends.injurymonitoringsystem.R;

import java.util.ArrayList;
import java.util.List;

import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.data.Acceleration;
import sendable.data.Position;

public class RequestDialog {
    private AlertDialog.Builder builder;
    private Activity activity;
    private RequestClickListener listener;

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
        startTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        startTime.setHint("Start time...");
        layout.addView(startTime);

        final EditText endTime = new EditText(activity);
        endTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        endTime.setHint("End time...");
        layout.addView(endTime);

        final EditText player = new EditText(activity);
        player.setInputType(InputType.TYPE_CLASS_NUMBER);
        player.setHint("Player ID...");
        layout.addView(player);

        final Spinner s = new Spinner(activity);
        s.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(activity, R.array.request_spinner_items, android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(arrayAdapter);

        layout.addView(s);
        builder.setView(layout);

        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        listener = new RequestClickListener(dialog, startTime, endTime, player, s, activity);

        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(listener);
        return dialog;
    }

    /*
     * public Request getRequest() {
     *     return listener.getRequest();
     * }
     *
     */
}

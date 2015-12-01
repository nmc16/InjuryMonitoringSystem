package com.abbasandfriends.injurymonitoringsystem.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.abbasandfriends.injurymonitoringsystem.R;

/**
 * Creates a dialog box that has multiple input fields to create
 * a {@link sendable.data.Request}.
 *
 * @version 1
 */
public class RequestDialog {
    private AlertDialog.Builder builder;
    private Activity activity;
    private RequestClickListener listener;

    public RequestDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity);
        this.activity = activity;
    }

    /**
     * Creates the dialog box with the multiple input fields
     * with the custom click listeners.
     *
     * @return Dialog box that can be shown on screen
     */
    public AlertDialog create() {
        // Set the title and message of the dialog
        builder.setTitle(R.string.request_dialog_title);
        builder.setMessage(R.string.request_dialog_msg);

        // Create a new layout to hold all the inputs
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create an input field for the start time and add it to the layout
        final EditText startTime = new EditText(activity);
        startTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        startTime.setHint("Start time...");
        layout.addView(startTime);

        // Create an input field for the end time and add it to the layout
        final EditText endTime = new EditText(activity);
        endTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        endTime.setHint("End time...");
        layout.addView(endTime);

        // Create an input field for the player ID and add it to the layout
        final EditText player = new EditText(activity);
        player.setInputType(InputType.TYPE_CLASS_NUMBER);
        player.setHint("Player ID...");
        layout.addView(player);

        // Create an input field for the data type and add it to the layout
        final Spinner s = new Spinner(activity);

        // Set the layout of the spinner
        s.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT));

        // Set the selection options for the spinner
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(activity,
                R.array.request_spinner_items, android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(arrayAdapter);
        layout.addView(s);

        // Set the view of the dialog to the layout created
        builder.setView(layout);

        // Set the buttons to no listeners before creation
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog box
        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the listener to one that does not close right away
        listener = new RequestClickListener(dialog, startTime, endTime, player, s, activity);
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(listener);

        return dialog;
    }
}

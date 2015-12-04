package com.abbasandfriends.injurymonitoringsystem;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.widget.Toast;


public class  ConnectionsActivity extends Activity {

    private EditText etIP, etPort;

    /**
     * Validates the IP address passed is of valid format
     * (ie XXX.XXX.XXX.XXX where values are not exceeded).
     *
     * @param ipAddress IP Address to validate the format
     * @return True if the IP Address is valid
     */
    public boolean validateIPAddress(String ipAddress) {
        final int upperLim = 255;
        final int lowerLim = 0;
        final int ipLength = 4;

        // Split the address by decimals
        String[] parts = ipAddress.split( "\\." );

        // Check each part so that it does not violate the upper and lower limit
        for (String s : parts) {
            int i = Integer.parseInt(s);
            if ( (i < lowerLim) || (i > upperLim) ) {
                return true;
            }
        }

        // Check the length is valid
        if ( parts.length != ipLength ) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        final Button connect;

        // Create the text fields for the IP and Port
        etIP = (EditText) findViewById(R.id.ipAddressHost);
        etPort = (EditText) findViewById(R.id.portNumberHost);
        connect = (Button) findViewById(R.id.connectButton);
        final int upperPort = 9999;
        final int lowPort = 1000;

        // Set the click listener to permeate the data
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddress = etIP.getText().toString();

                // Check the port is valid
                if (etPort.getText().toString().equals("")) {
                    Toast.makeText(ConnectionsActivity.this, "Provide a valid Port", Toast.LENGTH_SHORT).show();
                    return;
                } else if (ipAddress.equals("") || ipAddress.contains("..") || ipAddress.endsWith(".") || ipAddress.startsWith(".")) {
                    Toast.makeText(ConnectionsActivity.this, "Provide a valid IP", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate the IP Address
                if (validateIPAddress(ipAddress)) {
                    Toast.makeText(ConnectionsActivity.this, "IP address is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                int hostPort = Integer.valueOf(etPort.getText().toString());
                if (hostPort > upperPort || hostPort < lowPort) {
                    Toast.makeText(ConnectionsActivity.this, "Invalid Port, choose one between 1000 and 9999", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Return the data to the calling activity
                Intent intent = new Intent();
                intent.putExtra(MainAppActivity.HOST_IP, etIP.getText().toString());
                intent.putExtra(MainAppActivity.HOST_PORT, etPort.getText().toString());
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}

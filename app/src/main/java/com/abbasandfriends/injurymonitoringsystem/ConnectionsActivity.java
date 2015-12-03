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

    public boolean ValidateIPAddress(String ipAddress)
    {
        final int upperLim = 255;
        final int lowerLim = 0;
        final int ipLength = 4;
        String[] parts = ipAddress.split( "\\." );

        for ( String s : parts )
        {
            int i = Integer.parseInt( s );
            if ( (i < lowerLim) || (i > upperLim) )
            {
                return true;
            }
        }
        if ( parts.length != ipLength )
        {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        final Button connect;

        etIP = (EditText) findViewById(R.id.ipAddressHost);
        etPort = (EditText) findViewById(R.id.portNumberHost);
        connect = (Button) findViewById(R.id.connectButton);
        final int upperPort = 9999;
        final int lowPort = 1000;


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String ipAddress = etIP.getText().toString();

                if (etPort.getText().toString().equals("")) {
                    Toast.makeText(ConnectionsActivity.this, "Provide a valid Port", Toast.LENGTH_SHORT).show();
                    return;
                    //checks IP for valid input
                } else if (ipAddress.equals("") || ipAddress.contains("..") || ipAddress.endsWith(".") || ipAddress.startsWith(".")) {
                    Toast.makeText(ConnectionsActivity.this, "Provide a valid IP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ValidateIPAddress(ipAddress)) {
                    Toast.makeText(ConnectionsActivity.this, "IP address is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                //ipAddress = etIP.getText().toString();
                //need to validate ip address
                //if (ipAddress.ValidateIpAddress() = false);
                //look at bottom nic for method we could use

                int hostPort = Integer.valueOf(etPort.getText().toString());
                if (hostPort > upperPort || hostPort < lowPort) {
                    Toast.makeText(ConnectionsActivity.this, "Invalid Port, choose one between 1000 and 9999", Toast.LENGTH_SHORT).show();
                    return;
                }

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

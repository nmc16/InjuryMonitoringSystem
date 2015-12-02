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
    private Button connect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        etIP = (EditText) findViewById(R.id.ipAddressHost);
        etPort = (EditText) findViewById(R.id.portNumberHost);
        connect = (Button) findViewById(R.id.connectButton);
        final int upperPort = 9999;
        final int lowPort = 1000;






        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (etPort.getText().toString().equals("")) {
                    Toast.makeText(ConnectionsActivity.this, "Invalid Port", Toast.LENGTH_SHORT).show();
                    return;
                } else if (etIP.getText().toString().equals("")) {
                    Toast.makeText(ConnectionsActivity.this, "Invalid IP", Toast.LENGTH_SHORT).show();
                    return;
                }

                int hostPort = Integer.valueOf(etPort.getText().toString());
                if (hostPort > upperPort || hostPort < lowPort){
                    Toast.makeText(ConnectionsActivity.this, "Invalid Port, choose one between 1000 and 9999", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(MainAppActivity.HOST_IP, etIP.getText().toString());
                intent.putExtra(MainAppActivity.HOST_PORT, etPort.getText().toString());
                intent.putExtra(MainAppActivity.CLIENT_IP, "10.0.0.7");
                intent.putExtra(MainAppActivity.CLIENT_PORT, "8008");
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

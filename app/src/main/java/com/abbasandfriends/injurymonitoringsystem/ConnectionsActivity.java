package com.abbasandfriends.injurymonitoringsystem;


import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.widget.Toast;

import com.abbasandfriends.injurymonitoringsystem.alarm.AlarmDialog;
import com.abbasandfriends.injurymonitoringsystem.connection.ConnectionHandler;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import exception.CommunicationException;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.data.Service;

//unused


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
                //ipAddress = etIP.getText().toString();
                //need to validate ip address
                //if (ipAddress.ValidateIpAddress() = false);
                //look at bottom nic for method we could use


                int hostPort = Integer.valueOf(etPort.getText().toString());
                if (hostPort > upperPort || hostPort < lowPort){
                    Toast.makeText(ConnectionsActivity.this, "Invalid Port, choose one between 1000 and 9999", Toast.LENGTH_SHORT).show();
                    return;
                }





                ConnectionHandler connectionHandler = new ConnectionHandler();
                // Set up the connections to the database and the client connection to database
                try {
                    InetAddress inetAddress = InetAddress.getByName(etIP.getText().toString());
                    connectionHandler.host(hostPort, inetAddress);
                    Socket s = connectionHandler.acceptClient();
                    connectionHandler.setDataBaseReceive(s);

                    // TODO add text fields and get their input for client
                    //Socket r = connectionHandler.connectTo();
                    //connectionHandler.setDataBaseRequest(r);

                    // Add the connection handler to the context
                    ContextHandler.add(ContextHandler.HANDLER, connectionHandler);

                } catch (UnknownHostException e) {
                    Toast.makeText(ConnectionsActivity.this, "Could not connect to host: " +
                            e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } catch (CommunicationException e) {
                    Toast.makeText(ConnectionsActivity.this, "Could not set up receive connection: "
                            + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }


                runOnUiThread(new Runnable() {
                    //public Handler mHandler;

                    @Override
                    public void run() {
                        //Looper.prepare();
                        // Attempt to get the connection from context
                        Object o = ContextHandler.get(ContextHandler.HANDLER);

                        // Add message handler
                        //mHandler = new Handler();

                        // Check the object is valid
                        if (o == null || !(o instanceof ConnectionHandler)) {
                            Toast.makeText(ConnectionsActivity.this,
                                    "You must set-up the connections before you can send request!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // If it is valid, then attempt to send the request
                        try {
                            ConnectionHandler connectionHandler = (ConnectionHandler) o;
                            Socket s = connectionHandler.getDataBaseReceive();
                            List<Sendable> received = connectionHandler.receive(s);
                            parse(received);
                        } catch (CommunicationException e) {
                            Toast.makeText(ConnectionsActivity.this, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        //Looper.loop();
                    }


                    private void parse(List<Sendable> list) {
                        for (Sendable sendable : list) {
                            if (sendable instanceof Alarm) {
                                displayAlarm((Alarm) sendable);
                            } else if (sendable instanceof Service) {
                                decodeService(sendable);
                            } else {
                                // Do nothing
                            }
                        }
                    }

                    private void displayAlarm(Alarm alarm) {
                        AlertDialog alertDialog = new AlarmDialog(ConnectionsActivity.this).create(alarm);
                        alertDialog.show();
                    }

                    @SuppressWarnings("unchecked")
                    private void decodeService(Sendable sendable) {
                        Service service = (Service) sendable;
                        List<Sendable> list = service.getData();
                    }

                });
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

}
/**
 * public static boolean ValidateIPAddress(String ipAddress)
 {
 String[] parts = ipAddress.split( "\\." );
 if ( parts.length != 3 )
 {
 return false;
 }
 for ( String s : parts )
 {
 int i = Integer.parseInt( s );
 if ( (i < 0) || (i > 255) )
 {
 return false;
 }
 }
 return true;
 }

 */

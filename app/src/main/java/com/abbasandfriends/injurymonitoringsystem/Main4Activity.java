package com.abbasandfriends.injurymonitoringsystem;


import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.widget.Toast;
import java.io.IOException;
import java.net.Socket;

//unused
import exception.CommunicationException;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import android.os.Looper;
import android.widget.TextView;
import android.os.Bundle;
import android.content.Intent;

public class Main4Activity extends Activity {

    private EditText etIP, etPort;
    private Button connect;
    int port = 0;
    //boolean clientOn = false;
    private Socket client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);



        etIP = (EditText) findViewById(R.id.editText11);
        etPort = (EditText) findViewById(R.id.editText22);
        connect = (Button) findViewById(R.id.button1);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPort.getText().toString().equals("")){
                    Toast.makeText(Main4Activity.this, "Invalid Port", Toast.LENGTH_SHORT).show();
                } else {
                    port = Integer.parseInt(etPort.getText().toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                client = new Socket(etIP.getText().toString(), port);

                                //MainAppActivity.client = client;
                            } catch (IOException e) {
                                Toast.makeText(Main4Activity.this, "Invalid Address", Toast.LENGTH_SHORT).show();

                            }


                        }
                    }).start();
                    onBackPressed();
                }

                //port = Integer.parseInt(etPort.getText().toString());
                //MainAppActivity.clientIP = etIP.toString();
                //MainAppActivity.clientPort = port;
                //MainAppActivity.clientOn = true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
}

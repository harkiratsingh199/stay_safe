package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class verifyact extends AppCompatActivity {
    EditText email,password;
    TextView tv;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyact);
        email = (EditText) findViewById(R.id.Email);
        password = (EditText) findViewById(R.id.Code);
        bt = (Button) findViewById(R.id.btverify);
        tv= (TextView) findViewById(R.id.textView1);
        bt.setTextColor(Color.parseColor("#ffffff"));
        tv.setTextColor(Color.parseColor("#f29d09"));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new act()).start();
            }
        });

    }
    class act implements Runnable {
        Socket sock;
        PrintWriter pw;
        BufferedReader br;


        @Override
        public void run() {
            try {

                sock = new Socket(GlobalClass.ipAddressServer,8084);
                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String query = "/WithYou/mobileVerifyAccountServlet?email=" + email.getText() +  "&code=" + password.getText();
                query= query.replace(" ", "%20");
                pw.println("GET " + query + " HTTP/1.1");
                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
                pw.println("connection: close");
                pw.println("");
                pw.flush();
                while (true)
                {
                    String p = br.readLine();
                    if(p.equals(""))
                    {
                        break;
                    }
                }
                final String s = br.readLine();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();

                        if(s.equals("Verification Successful"))
                        {
                            SharedPreferences pref =getSharedPreferences("pref1", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=pref.edit();
                            editor.putString("email",email.getText().toString());
                            editor.commit();
                            verifyact.this.finish();
                            Intent it = new Intent(verifyact.this, MainActivity.class);
                            startActivity(it);
                        }
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

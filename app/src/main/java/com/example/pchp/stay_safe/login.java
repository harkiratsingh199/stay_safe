package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class login extends AppCompatActivity {
    EditText email,password;
    TextView tv,tv1;
    Button bt;
    String h="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences pref = getSharedPreferences("pref1", Context.MODE_PRIVATE);
        String emaill = pref.getString("email","null");
        if(!emaill.equals("null"))
        {
            startActivity(new Intent(login.this,MainActivity.class));
            login.this.finish();
        }

        email= (EditText) findViewById(R.id.etEmail);
   password= (EditText) findViewById(R.id.etPassword);
   bt= (Button) findViewById(R.id.btLogIn);
        tv= (TextView) findViewById(R.id.textView);
//        tv1= (TextView) findViewById(R.id.tt);
        tv1.setTextColor(Color.parseColor("#ffffff"));
        bt.setTextColor(Color.parseColor("#ffffff"));
        tv.setTextColor(Color.parseColor("#f29d09"));
     h=email.getText()+"";

    bt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new log()).start();
        }
    });
}

//public void go4(View v)
//{
//    startActivity(new Intent(login.this,verifyact.class));
//    login.this.finish();
//}
    class log implements Runnable {
        Socket sock;
        PrintWriter pw;
        BufferedReader br;


        @Override
        public void run() {
//            try {
//
//                sock = new Socket(GlobalClass.ipAddressServer,8084);
////                sock = new Socket("192.168.43.66", 8084);
////                sock = new Socket("192.168.178.52", 8084);
//
////                sock = new Socket("192.168.2.106", 8084);
//                pw = new PrintWriter(sock.getOutputStream());
//                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//                String query = "/WithYou/mlogin?email=" + email.getText()+"" +  "&password=" + password.getText()+"";
//                query= query.replace(" ", "%20");
//                pw.println("GET " + query + " HTTP/1.1");
//                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
////                pw.println("Host: 192.168.43.66 :8084");
////                pw.println("Host: 192.168.2.106:8084");
//                pw.println("connection: close");
//                pw.println("");
//                pw.flush();
//                while (true) {
//                    String s = br.readLine();
//                    if (s == null || s.isEmpty()) {
//                        break;
//                    }
//                }
//                final String s = br.readLine();
//                runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            if(s.equals("Login Successful"))
         {
          SharedPreferences pref =getSharedPreferences("pref1", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor=pref.edit();
          editor.putString("email",email.getText().toString());
          editor.commit();
          login.this.finish();
         Intent it = new Intent(login.this, MainActivity.class);
         startActivity(it);
           }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}

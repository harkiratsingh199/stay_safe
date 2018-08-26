package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Sign_up extends AppCompatActivity {
    EditText first,last,phone,ans,email,pass,confirm;
    TextView tv,tv1;
    Spinner ques;
    Button bt;
    ArrayList<String> al;
    String pa="";
    String ca="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        al=new ArrayList<>();
        first=(EditText)findViewById(R.id.name);
        last=(EditText)findViewById(R.id.last);
        phone=(EditText)findViewById(R.id.phoneno);
        ans=(EditText)findViewById(R.id.answer);
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.password);
        confirm=(EditText)findViewById(R.id.confirm);
        ques= (Spinner) findViewById(R.id.ques);
        bt= (Button) findViewById(R.id.bt1);
        tv= (TextView) findViewById(R.id.textView1);
        tv1= (TextView) findViewById(R.id.signin);
        tv1.setTextColor(Color.parseColor("#ffffff"));
        bt.setTextColor(Color.parseColor("#ffffff"));
        tv.setTextColor(Color.parseColor("#f29d09"));
        al.add("What is your Dog Name");
        al.add("What is your Father Name");
        al.add("What is your Birth Place");
        al.add("Which is your Favourite City");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(Sign_up.this,android.R.layout.simple_list_item_1,al);

        ques.setAdapter(adapter);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pa=pass.getText()+"";
                ca=confirm.getText()+"";
                if(!pa.equals(ca)){
                    Toast.makeText(getBaseContext(), "Password Combination wrong", Toast.LENGTH_SHORT).show();
                }

                else {

                    new Thread(new signupuser()).start();
                }
            }
        });
    }
public void go3(View v)
{
    startActivity(new Intent(Sign_up.this,login.class));
    Sign_up.this.finish();
}
    class signupuser implements Runnable {
        Socket sock;
        PrintWriter pw;
        BufferedReader br;


        @Override
        public void run() {
            try {
                sock = new Socket(GlobalClass.ipAddressServer,8084);
//                sock = new Socket("192.168.43.66", 8084);
                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                String query = "/WithYou/mobileusersignup?email=" + email.getText() + "&first=" + first.getText() + "&last=" + last.getText() + "&phoneno=" + phone.getText() + "&password=" + pass.getText() + "&confirm=" + confirm.getText() + "&ans=" + ans.getText() + "&ques=" + (String)ques.getSelectedItem();
                query= query.replace(" ", "%20");
                pw.println("GET " + query + " HTTP/1.1");
                pw.println("host: "+GlobalClass.ipAddressServer+":8084");
//                pw.println("host: 192.168.43.97:8084");
                pw.println("connection: close");
                pw.println("");
                pw.flush();
                while (true) {
                    String p = br.readLine();
                    if(p==null || p.isEmpty()) {
                        break;
                    }

                }
                final String s = br.readLine();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                        if(s.equals("Signup successfully"
                                + "Please Verify your account"))
                        {
                            Sign_up.this.finish();
                            Intent it =new Intent(Sign_up.this,verifyact.class);
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


package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class firstscreen extends AppCompatActivity {
    Button bt1,bt2;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);
        bt1= (Button) findViewById(R.id.bcreate);
        bt2= (Button) findViewById(R.id.login);
        tv= (TextView) findViewById(R.id.quotes);
        bt2.setTextColor(Color.parseColor("#ffffff"));
        tv.setTextColor(Color.parseColor("#ffffff"));
        SharedPreferences pref = getSharedPreferences("pref1", Context.MODE_PRIVATE);
        String emaill = pref.getString("email","null");
        if(!emaill.equals("null"))
        {
            startActivity(new Intent(firstscreen.this,MainActivity.class));
            firstscreen.this.finish();
        }
    }
    public void go1(View v)
    {
        startActivity(new Intent(getBaseContext(),Sign_up.class));
    }
    public void go2(View v)
    {
        startActivity(new Intent(getBaseContext(), login.class));
    }
}

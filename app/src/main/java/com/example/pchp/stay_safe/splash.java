package com.example.pchp.stay_safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class splash extends Activity {
    long START_UP_DELAY = 3000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent loginPage = new Intent(splash.this, login.class);
                startActivity(loginPage);
            }
        },START_UP_DELAY);

//    protected void onPause()
//        {
//        // TODO Auto-generated method stub
//        super.onPause();
//        this.finish();
//         }

}

}




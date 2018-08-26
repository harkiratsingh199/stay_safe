package com.example.pchp.stay_safe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MyReceiver2 extends BroadcastReceiver {

    int count  = 0;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.SCREEN_OFF"))
        {
            count++;
        }
        else if (intent.getAction().equals("android.intent.action.SCREEN_ON"))
        {
            count++;
        }
        if(count >3)
        {
            Toast.makeText(context, "power button clicked", Toast.LENGTH_SHORT).show();

            Double lat = GlobalClass.lat;
             Double lng = GlobalClass.lng;

            SmsManager smsManager = SmsManager.getDefault();

            Cursor c = GlobalClass.db.query("contacts", null, null, null, null, null, null);

            if(c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    String name = c.getString(c.getColumnIndex("contactname"));
                    long phone = c.getLong(c.getColumnIndex("contactno"));
                    smsManager.sendTextMessage(phone+"", null,
                            "I am in Danger location, My google location is http://maps.google.com/?q="+lat+","+lng, null, null);


                    Toast.makeText(context, "Sms Sent to "+name + "," + phone, Toast.LENGTH_SHORT).show();
                    c.moveToNext();
                }
            }
            else
            {
                Toast.makeText(context,"Sms failed", Toast.LENGTH_SHORT).show();

            }
            count = 0;
        }
    }
}

package com.example.pchp.stay_safe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class MyReceiver1 extends BroadcastReceiver {



    public MyReceiver1()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.location.PROVIDERS_CHANGED"))
        {
            if(GlobalClass.manager!=null) {
                if (GlobalClass.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        || (GlobalClass.manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
                    Intent it = new Intent(context, MyService1.class);
                    it.setAction("start_tracking");
                    context.startService(it);
                }
            }
        }

    }

}

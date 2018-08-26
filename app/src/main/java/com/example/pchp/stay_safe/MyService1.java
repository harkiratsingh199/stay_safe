package com.example.pchp.stay_safe;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyService1 extends Service {
    MyLocationListener listener;

    MyReceiver2 rec=new MyReceiver2();

    public MyService1() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startid) {
        if (intent.getAction().equals("start_tracking")) {
            Toast.makeText(MyService1.this, "SERVICE STARTED", Toast.LENGTH_SHORT).show();

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(rec, filter);

            GlobalClass.manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            listener = new MyLocationListener();

//            GlobalClass.manager .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
//            GlobalClass.manager .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            try
            {
                GlobalClass.manager .requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 2.0f,listener );
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            startForeground(4523,generateNotification());

        }
        else if(intent.getAction().equals("stop_tracking"))
        {
            Toast.makeText(MyService1.this, "SERVICE STOPPED", Toast.LENGTH_SHORT).show();

            GlobalClass.manager .removeUpdates(listener);


            listener = null;

            turnGPSOff();

            unregisterReceiver(rec);
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    Notification generateNotification()
    {
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);



        Intent stopIntent = new Intent(this, MyService1.class);
        stopIntent.setAction("stop_tracking");
        PendingIntent pstopIntent = PendingIntent.getService(this, 0, stopIntent, 0);


//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_radar);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher));
        builder.setContentTitle("Spot Me");
        builder.setContentText("You are being tracked");

        builder.setContentInfo("Live Tracking");
        builder.setContentIntent(pendingIntent);
        int color = getResources().getColor(R.color.colorAccentt);
        builder.setColor(color);
        builder.setAutoCancel(true);


        builder.addAction(R.drawable.icon_stop, "Stop", pstopIntent);

        Notification n = builder.build();
        n.flags = Notification.FLAG_NO_CLEAR;
        return n;
    }

    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

//            new Thread(new SendLatLng(latitude,longitude)).start();

            GlobalClass.lat = latitude;
            GlobalClass.lng = longitude;

            Intent it = new Intent("sending_location");
            it.putExtra("lat",latitude);
            it.putExtra("lng",longitude);
            sendBroadcast(it);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void turnGPSOff()
    {
        String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps"))
        {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings","com.android.settings.widget.SettingAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


}

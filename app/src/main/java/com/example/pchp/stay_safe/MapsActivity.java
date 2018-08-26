package com.example.pchp.stay_safe;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    boolean flag = false;
    private GoogleMap mMap;
    MyLocalReceiver receiver;
    TextView tvLatLng;
    LatLng current  ;
    PolylineOptions polylineOptions;
    int p = 0;
    Button bt_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        tvLatLng = (TextView) findViewById(R.id.tvLatLng);
        bt_sms = (Button) findViewById(R.id.bt_sms);
        bt_sms.setTextColor(Color.parseColor("#ffffff"));
        bt_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < GlobalClass.alContacts.size(); i++) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(GlobalClass.alContacts.get(i).contactNo, null,
                                "I am in Danger location, My google location is http://maps.google.com/?q=" + current, null, null);

                        Toast.makeText(MapsActivity.this, "Sms Sent to " + GlobalClass.alContacts.get(i).contactNo, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MapsActivity.this, "Sms failed", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

        GlobalClass.manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new MyLocalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sending_location");
        registerReceiver(receiver, filter);

        GlobalClass.manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = GlobalClass.manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = GlobalClass.manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        if (!gps_enabled || !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Do you want to enable GPS ?");
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);


                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        } else {
            Intent it = new Intent(getBaseContext(), MyService1.class);
            it.setAction("start_tracking");
            startService(it);
        }


    }


    class MyLocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            p++;
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            current = new LatLng(lat, lng);
            if (p == 4)
            {
                for (int i = 0; i < GlobalClass.alLocations.size(); i++)
                {
                    double distance = distFrom(lat, lng, GlobalClass.alLocations.get(i).lat, GlobalClass.alLocations.get(i).lng);

                    tvLatLng.setText(distance + "");

                    if (distance <= 300) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(GlobalClass.alLocations.get(i).lat, GlobalClass.alLocations.get(i).lng))
                                .title(GlobalClass.alLocations.get(i).address));
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(GlobalClass.alLocations.get(i).lat, GlobalClass.alLocations.get(i).lng))
                                .radius(300)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE));
                        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(2000);
                    }
                }

                new Thread(new SendLatLng(lat,lng)).start();

                p = 0;
            }

            mMap.addPolyline(polylineOptions.add(current));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        polylineOptions = new PolylineOptions();
        polylineOptions.width(8);
        polylineOptions.color(Color.BLUE);
        polylineOptions.geodesic(true);
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (earthRadius * c);
        return dist;
    }


    class SendLatLng implements Runnable{

        double lat;
        double lng;

        public SendLatLng(double lat, double lng)
        {
            this.lat = lat;
            this.lng = lng;
        }
        @Override
        public void run() {
            try {
                Socket sock;
                PrintWriter pw;
                BufferedReader br;



                sock = new Socket(GlobalClass.ipAddressServer,8084);
//                sock = new Socket("192.168.43.66", 8084);

                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                SharedPreferences pref = getSharedPreferences("pref1", Context.MODE_PRIVATE);
                String email = pref.getString("email", "null");

                String qq = "/WithYou/mAddLatLng?email=" + email + "&lat=" + lat + "&lng=" + lng;
                qq = qq.replace(" ", "%20");
                final String x = qq;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, x, Toast.LENGTH_SHORT).show();
                    }
                });
                pw.println("GET " + qq + " HTTP/1.1");
//                pw.println("host: 192.168.43.66:8084");

                pw.println("host: "+GlobalClass.ipAddressServer+":8084");
                pw.println("connection: close");
                pw.println();
                pw.flush();

                while (true) {
                    String s = br.readLine();
                    if (s == null || s.isEmpty()) {
                        break;
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MapsActivity2 extends FragmentActivity {

    GoogleMap googleMap;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        googleMap = supportMapFragment.getMap();

        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                  latitude=latLng.latitude;
                longitude=latLng.longitude;
                new Thread(new SendLatLng(latitude,longitude)).start();
                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }
        });
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

                String qq = "/WithYou/maddress?email=" + email + "&lat=" + lat + "&lng=" + lng;
                qq = qq.replace(" ", "%20");
                final String x = qq;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity2.this, x, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
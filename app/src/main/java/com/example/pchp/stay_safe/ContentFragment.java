package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * A fragment that launches other parts of the demo application.
 */
public class ContentFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    Button btm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.content_fragment, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
//        btm=(Button)v.findViewById(R.id.btm);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        new Thread(new loc()).start();

        // Perform any camera updates here
        return v;
    }

    class loc implements Runnable {
        Socket sock;
        PrintWriter pw;
        BufferedReader br;


        @Override
        public void run() {
            try {
                SharedPreferences pref = getActivity().getSharedPreferences("pref1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                String h = pref.getString("email", "null");
                editor.commit();


//                sock = new Socket("192.168.43.66", 8084);
                sock = new Socket(GlobalClass.ipAddressServer,8084);

//                sock = new Socket("192.168.2.106", 8084);
                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String query = "/WithYou/mviewlocation?email=" + h;
                query = query.replace(" ", "%20");
                pw.println("GET " + query + " HTTP/1.1");
                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
//                pw.println("host: 192.168.43.66 :8084");
//                pw.println("host: 192.168.2.106:8084");
                pw.println("connection: close");
                pw.println("");
                pw.flush();
                while (true) {
                    String p = br.readLine();
                    if (p == null || p.isEmpty()) {
                        break;
                    }

                }
                while(true) {
                    String s = br.readLine();
                    if(s==null || s.isEmpty())
                        break;
                    StringTokenizer st = new StringTokenizer(s, "^");

                   String lat  = st.nextToken();
                    String lon = st.nextToken();
                    String add = st.nextToken();
                    String name= st.nextToken();
                    String id = st.nextToken();
                    String lname = st.nextToken();

                    GlobalClass.alLocations.add(new DangerLocations(Double.parseDouble(lat),
                            Double.parseDouble(lon),add,name,Integer.parseInt(id),lname));


                    // adding marker
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<GlobalClass.alLocations.size(); i++)
                            {
//                                Toast.makeText(getActivity(), " "+GlobalClass.alLocations.size(), Toast.LENGTH_SHORT).show();
                                LatLng point = new LatLng(GlobalClass.alLocations.get(i).lat,GlobalClass.alLocations.get(i).lng);
                                googleMap.addMarker(new MarkerOptions().position(point).title(GlobalClass.alLocations.get(i).address+"\nMarked BY:"+ GlobalClass.alLocations.get(i).marked_by));
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(point).zoom(15).build();
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                            }


                            }
                    });


                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
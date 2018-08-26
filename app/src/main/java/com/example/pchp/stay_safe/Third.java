package com.example.pchp.stay_safe;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class Third extends Fragment {


    public Third() {
        // Required empty public constructor
    }
    MapView mMapView;
    private GoogleMap googleMap;
    Button btm;
    TextView tv;
    LatLng  current;
    ArrayList<LatLng> al=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_third2, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
//        btm=(Button)v.findViewById(R.id.btm);
        mMapView.onCreate(savedInstanceState);
        btm=(Button)v.findViewById(R.id.btsubmit);
        tv=(TextView)v.findViewById(R.id.tv11);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new loc()).start();

            }
        });


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
                String query = "/WithYou/fetchlatlngonSpecificDateServlet?email=" + h+"&date="+tv.getText().toString();
                query = query.replace(" ", "%20");
                pw.println("GET " + query + " HTTP/1.1");
                pw.println("host: 192.168.43.66 :8084");
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

                    double lat  =Double.parseDouble(st.nextToken());
                    double lon  =Double.parseDouble(st.nextToken());
                 al.add(new LatLng(lat, lon));
                    current=new LatLng(lat, lon);
//                    googleMap.addPolyline(new PolylineOptions().width(8).color(Color.BLUE).add(current));
//
////                                Toast.makeText(getActivity(), " "+GlobalClass.alLocations.size(), Toast.LENGTH_SHORT).show();
//                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                            .target(current).zoom(18).build();
//                    googleMap.animateCamera(CameraUpdateFactory
//                            .newCameraPosition(cameraPosition));
                    // adding marker
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < al.size() - 1; i = i + 1) {
                                current = al.get(i);
                                googleMap.addPolyline(
                                        new PolylineOptions().width(8).color(Color.BLUE).add(al.get(i)));

                                Toast.makeText(getActivity(), " " + al.size() + current, Toast.LENGTH_SHORT).show();
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(current).zoom(18).build();
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                                googleMap.addMarker(new MarkerOptions().position(current).title("hello"));

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

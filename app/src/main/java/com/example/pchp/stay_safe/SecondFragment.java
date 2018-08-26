package com.example.pchp.stay_safe;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

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
public class SecondFragment extends Fragment {
TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
     Button btrack;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};


    public SecondFragment() {
        // Required empty public constructor
    }
    public void onResume() {
        super.onResume();
        tv1= (TextView)getActivity().findViewById(R.id.fname);
        tv3= (TextView)getActivity(). findViewById(R.id.emaill);
        tv4= (TextView) getActivity().findViewById(R.id.numberr);
//        tv5= (TextView) getActivity().findViewById(R.id.al);
        tv6= (TextView) getActivity().findViewById(R.id.dev);
        tv7= (TextView) getActivity().findViewById(R.id.wel);
        btrack= (Button) getActivity().findViewById(R.id.btrack);
        tv1.setTextColor(Color.parseColor("#303F9F"));
        tv7.setTextColor(Color.parseColor("#303F9F"));
        tv3.setTextColor(Color.parseColor("#303F9F"));
        tv4.setTextColor(Color.parseColor("#303F9F"));
        tv5.setTextColor(Color.parseColor("#303F9F"));
        tv6.setTextColor(Color.parseColor("#000000"));
        btrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), MapsActivity.class);
                startActivity(it);
            }
        });
        new Thread(new info()).start();
//        new Thread(new FetchProfilePic()).start();
//        carouselView = (CarouselView) getActivity().findViewById(R.id.carouselView);
//        carouselView.setImageListener(imageListener);
//
//        carouselView.setPageCount(sampleImages.length);



    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_second, container, false);

    return v;
    }

    class info implements Runnable {
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

                sock = new Socket(GlobalClass.ipAddressServer,8084);
//                sock = new Socket("192.168.43.66", 8084);
//                sock = new Socket("192.168.2.106", 8084);
                pw = new PrintWriter(sock.getOutputStream());
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String query = "/WithYou/mfetchinfo?email=" +h;
                query= query.replace(" ", "%20");
                pw.println("GET " + query + " HTTP/1.1");
                pw.println("host: " +GlobalClass.ipAddressServer+ ":8084");
//                pw.println("Host: 192.168.43.66 :8084");
//                pw.println("Host: 192.168.2.106:8084");
                pw.println("connection: close");
                pw.println("");
                pw.flush();
                while (true) {
                    String s = br.readLine();
                    if (s == null || s.isEmpty()) {
                        break;
                    }
                }
                final String s = br.readLine();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringTokenizer st=new StringTokenizer(s,"^");
                        String f=st.nextToken();
                        String l=st.nextToken();
                        String e=st.nextToken();
                        String n=st.nextToken();
                        tv1.setText(f+" "+l);
                        tv3.setText(e);
                        tv4.setText(n);
//                        Toast.makeText(getActivity().getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

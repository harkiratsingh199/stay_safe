package com.example.pchp.stay_safe;




import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment
{
    WebView wv1;

    public BlankFragment()
    {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
 View v= inflater.inflate(R.layout.fragment_blank, container, false);
              return v;

    }
    public void onResume() {
        super.onResume();
//        wv1 = (WebView) getActivity().findViewById(R.id.wv1);
//
//        wv1.loadUrl("file:///android_asset/hello.html");

    }


}



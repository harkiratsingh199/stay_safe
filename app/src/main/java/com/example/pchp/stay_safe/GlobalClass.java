package com.example.pchp.stay_safe;

/**
 * Created by PC HP on 5/3/2016.
 */

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;

        import java.util.ArrayList;

public class GlobalClass
{
    static double lat ;
    static double lng ;
    static SQLiteDatabase db;
    static ArrayList<DangerLocations> alLocations = new ArrayList<>();
    static ArrayList<MyContacts> alContacts = new ArrayList<>();
    static LocationManager manager;
//    static String ipAddressServer = "192.168.43.66";       //  hotspot adress
//    static String ipAddressServer = "192.168.43.137";       //  hotspot adress
//    static String ipAddressServer = "192.168.2.104";       //  hotspot adress
//    static String ipAddressServer = "192.168.178.52";       //  hotspot adress
    static String ipAddressServer = "172.20.10.4";
}

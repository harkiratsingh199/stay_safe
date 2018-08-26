package com.example.pchp.stay_safe;

/**
 * Created by PC HP on 5/4/2016.
 */
public class DangerLocations {
    Double lat,lng;
    String address,marked_by,locationName;
    int id;


    public DangerLocations(Double lat,Double lng,String address,String marked_by,int id,String locationName)
    {
        this.lat = lat;
        this.lng= lng;
        this.address =address;
        this.marked_by =marked_by;
        this.id =id;
        this.locationName=locationName;
    }
}

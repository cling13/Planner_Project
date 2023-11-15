package com.example.plannerproject010;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyGoogleMap {
    GoogleMap gMap;

    MyGoogleMap(GoogleMap gMap){
        this.gMap=gMap;
    }


    void setgMap()
    {
        LatLng defaultLocation = new LatLng(37.541, 126.986);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,12));

    }

    void addMark(LatLng latLng, String title)
    {
        gMap.addMarker(new MarkerOptions().position(latLng).title(title));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15)); //마커로 카메라 이동
    }

    void markClear()
    {
        gMap.clear();
    }

}

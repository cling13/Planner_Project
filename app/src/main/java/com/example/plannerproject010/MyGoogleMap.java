package com.example.plannerproject010;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MyGoogleMap {
    GoogleMap gMap;

    MyGoogleMap(GoogleMap gMap){
        this.gMap=gMap;
    }


    void setgMap()
    {
        LatLng defaultLocation = new LatLng(35.9450, 126.6828);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,16));

    }

    void addMark(LatLng latLng, String title)
    {
        gMap.addMarker(new MarkerOptions().position(latLng).title(title));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,30)); //마커로 카메라 이동
    }

    void markClear()
    {
        gMap.clear();
    }

    public void moveCamera(CameraUpdate newLatLng) {
        gMap.moveCamera(newLatLng);
    }

    public void addPolyline(PolylineOptions polylineOptions) {
        gMap.addPolyline(polylineOptions);
    }

    public void clear() {
        gMap.clear();
    }
}

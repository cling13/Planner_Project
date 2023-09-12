package com.example.plannerproject010;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class listClass implements Serializable {
    byte[] image;
    String name;
    String address;
    double lat;
    double lng;

    listClass(Bitmap image, String name,String address,LatLng latLng)
    {
        this.name=name;
        this.address=address;
        this.lat=latLng.latitude;
        this.lng=latLng.longitude;
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        this.image=stream.toByteArray();
    }


    public LatLng getlatLng() {
        return new LatLng(lat,lng);
    }

    public Bitmap getImage() {
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
        return bitmap;
    }

    public void setImage(Bitmap image) {
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        this.image=stream.toByteArray();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

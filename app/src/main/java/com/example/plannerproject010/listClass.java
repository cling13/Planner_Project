package com.example.plannerproject010;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class listClass implements Serializable{
    byte[] image;
    String name;
    String address;
    double lat;
    double lng;
    String btnName;
    String id;

    listClass(Bitmap image, String name,String address,LatLng latLng,String btnName,String id)
    {
        this.name=name;
        this.address=address;
        this.lat=latLng.latitude;
        this.lng=latLng.longitude;
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,stream);
        this.image=stream.toByteArray();
        this.btnName=btnName;
        this.id=id;
    }


    public String getId()
    {
        return id;
    }
    public LatLng getlatLng() {
        return new LatLng(lat,lng);
    }

    public Bitmap getImage() {
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getBtnName(){return btnName;}

    public void setBtnName(String btnName)
    {
        this.btnName=btnName;
    }
}

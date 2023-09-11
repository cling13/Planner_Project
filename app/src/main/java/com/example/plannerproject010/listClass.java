package com.example.plannerproject010;

import android.graphics.Bitmap;

public class listClass {
    Bitmap image;
    String name;
    String address;

    listClass(Bitmap image, String name,String address)
    {
        this.image=image;
        this.name=name;
        this.address=address;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image=image;
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

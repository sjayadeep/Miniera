package com.artincodes.miniera.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by jayadeep on 20/7/15.
 */
public class AppPack {

    public String label;
    public String package_name;
    public Drawable icon;

    public AppPack(String label,String package_name,Drawable icon){
        this.label = label;
        this.package_name = package_name;
        this.icon = icon;
    }
}

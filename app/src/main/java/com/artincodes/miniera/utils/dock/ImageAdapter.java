package com.artincodes.miniera.utils.dock;

/**
 * Created by jayadeep on 1/1/15.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.artincodes.miniera.R;


public class ImageAdapter extends BaseAdapter {
    private Context context;
    String[] packageNames;
    Drawable[] icons;
    //private final String[] mobileValues;

    public ImageAdapter(Context context,String[] packageNames,Drawable[] icons) {
        this.context = context;
        this.packageNames= packageNames;
        this.icons = icons;
        //this.mobileValues = mobileValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.dock_item, null);

            // set value into textview

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.dock_icon_image);

            //String mobile = mobileValues[position];

            if (position ==2) {
//                imageView.setImageResource(R.drawable.ic_apps_white)
//                imageView.setImageResource();

            } else {

                imageView.setImageDrawable(icons[position]);
            }

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

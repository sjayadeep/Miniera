package com.artincodes.miniera.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artincodes.miniera.R;

public class ListAdapterCommon extends ArrayAdapter<String> {

    private final Context context;
    private final String[] optionTitle;
    private final Integer[] optionIcon;
    String color=null;

    static class ViewHolder {
        TextView title;
        ImageView icon;
    }

    public ListAdapterCommon(Context context, String[] optionTitle, Integer[] optionIcon, String color) {
        super(context, R.layout.list_row, optionTitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.optionTitle = optionTitle;
        this.optionIcon = optionIcon;
        this.color = color;
    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_row, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.more_option_text);
            viewHolder.icon = (ImageView) view.findViewById(R.id.more_option_icon);
            viewHolder.title.setTextColor(Color.parseColor(color));
            // The tag can be any Object, this just happens to be the ViewHolder
            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.title.setText(optionTitle[position]);
        viewHolder.icon.setImageResource(optionIcon[position]);


        return view;
    }
}
package com.artincodes.miniera.utils.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.artincodes.miniera.R;

public class ToDoListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] optionTitle;
    String color=null;

    static class ViewHolder {
        TextView title;
    }

    public ToDoListAdapter(Context context, String[] optionTitle) {
        super(context, R.layout.task_view, optionTitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.optionTitle = optionTitle;
    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.task_view, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.taskTextView);
            //viewHolder.title.setTextColor(Color.parseColor(color));
            // The tag can be any Object, this just happens to be the ViewHolder
            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.title.setText(optionTitle[position]);
        //viewHolder.icon.setImageResource(optionIcon[position]);


        return view;
    }
}
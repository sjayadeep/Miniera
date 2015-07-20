package com.artincodes.miniera.utils.widgetutils;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.artincodes.miniera.fragments.WidgetFragment;

/**
 * Created by jayadeep on 19/7/15.
 */
public class WidgetRecyclerAdapter extends RecyclerView.Adapter<WidgetRecyclerAdapter.ViewHolder>{

    String[] widgetID;
    Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
//        ViewGroup widgetLayout;
        AppWidgetHostView hostView;

        public ViewHolder(AppWidgetHostView itemView) {
            super(itemView);
            hostView = itemView;
        }
    }

    public WidgetRecyclerAdapter(Context context,String[] widgetID){

        this.context = context;

        this.widgetID = widgetID;

    }

    @Override
    public WidgetRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AppWidgetHostView appWidgetHostView = new AppWidgetHostView(context);

        appWidgetHostView.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ViewHolder viewHolder = new ViewHolder(appWidgetHostView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WidgetRecyclerAdapter.ViewHolder holder, int position) {

        holder.hostView = setupWidget(Integer.parseInt(widgetID[position]));



    }

    @Override
    public int getItemCount() {
        try {
            return widgetID.length;
        }catch (NullPointerException e){
            return 0;
        }
    }


    public AppWidgetHostView setupWidget(int widgetId){

        AppWidgetProviderInfo appWidgetInfo = WidgetFragment.mAppWidgetManager.getAppWidgetInfo(widgetId);
        String label = appWidgetInfo.label;
//        appWidgetInfo.configure;


        AppWidgetHostView hostView;
        Log.i("WIDGET LABEL", label);


        hostView = WidgetFragment.mAppWidgetHost.createView(context, widgetId, appWidgetInfo);
        hostView.setAppWidget(widgetId, appWidgetInfo);
//        hostView.setMinimumHeight(300);
        if (label.equals("Gmail") || label.equals("Hangouts")){
            hostView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            300));
        }else {
            hostView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
        }

//        hostView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.v("WIDGET", "CHILD TOUCH");
//
//                // Disallow the touch request for parent scroll on touch of  child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });




        Log.i("WIDGET", "The widget size is: " + hostView.getWidth() + "*" + hostView.getHeight());
        return hostView;

    }
}

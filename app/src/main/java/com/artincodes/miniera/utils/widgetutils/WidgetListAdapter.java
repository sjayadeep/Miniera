//package com.artincodes.miniera.utils.widgetutils;
//
//import android.appwidget.AppWidgetHostView;
//import android.appwidget.AppWidgetProviderInfo;
//import android.content.Context;
//import android.graphics.Color;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.artincodes.miniera.R;
//import com.artincodes.miniera.fragments.LauncherFragment;
//import com.artincodes.miniera.fragments.WidgetFragment;
//
//public class WidgetListAdapter extends ArrayAdapter<String> {
//
//    private final Context context;
//    private final String[] widgetID;
//
//
//    static class ViewHolder {
//        ViewGroup widgetLayout;
//        AppWidgetHostView hostView;
//
//    }
//
//    public WidgetListAdapter(Context context, String [] widgetID) {
//        super(context, R.layout.widget_item_layout,widgetID);
//        // TODO Auto-generated constructor stub
//
//        this.context = context;
//        this.widgetID = widgetID;
//    }
//
//    public View getView(int position, View view, final ViewGroup parent) {
//
//        ViewHolder viewHolder;
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        Log.i("WIDGET ID",": " + widgetID[position]);
//
//
//        if (view == null) {
//            view = layoutInflater.inflate(R.layout.widget_item_layout, null);
//
//            viewHolder = new ViewHolder();
//            viewHolder.hostView = new AppWidgetHostView(context);
//            viewHolder.widgetLayout = (ViewGroup)view.findViewById(R.id.widget_layout);
////            viewHolder.title = (TextView) view.findViewById(R.id.more_option_text);
////            viewHolder.icon = (ImageView) view.findViewById(R.id.more_option_icon);
////            viewHolder.title.setTextColor(Color.parseColor(color));
////            // The tag can be any Object, this just happens to be the ViewHolder
////            view.add
////            parent.addView(viewHolder.hostView);
//            view.setTag(viewHolder);
//
//        } else {
//
//            viewHolder = (ViewHolder) view.getTag();
//
//        }
//
//
//        viewHolder.hostView = setupWidget(Integer.parseInt(widgetID[position]));
//        viewHolder.hostView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                parent.requestDisallowInterceptTouchEvent(true);
//
//                int action = event.getActionMasked();
//
//                switch (action) {
//                    case MotionEvent.ACTION_UP:
//                        parent.requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//
//                return false;
//            }
//        });
//        viewHolder.widgetLayout.addView(viewHolder.hostView);
//
////        viewHolder.title.setText(optionTitle[position]);
////        viewHolder.icon.setImageResource(optionIcon[position]);
//
//
//        return view;
//    }
//
//    public AppWidgetHostView setupWidget(int widgetId){
//
//        AppWidgetProviderInfo appWidgetInfo = WidgetFragment.mAppWidgetManager.getAppWidgetInfo(widgetId);
//        String label = appWidgetInfo.label;
////        appWidgetInfo.configure;
//
//
//        AppWidgetHostView hostView;
//        Log.i("WIDGET LABEL", label);
//
//
//        hostView = WidgetFragment.mAppWidgetHost.createView(context, widgetId, appWidgetInfo);
//        hostView.setAppWidget(widgetId, appWidgetInfo);
////        hostView.setMinimumHeight(300);
//        if (label.equals("Gmail") || label.equals("Hangouts")){
//            hostView.setLayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            300));
//        }else {
//            hostView.setLayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT));
//        }
//
////        hostView.setOnTouchListener(new View.OnTouchListener() {
////
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                Log.v("WIDGET", "CHILD TOUCH");
////
////                // Disallow the touch request for parent scroll on touch of  child view
////                v.getParent().requestDisallowInterceptTouchEvent(true);
////                return false;
////            }
////        });
//
//
//
//
//        Log.i("WIDGET", "The widget size is: " + hostView.getWidth() + "*" + hostView.getHeight());
//        return hostView;
//
//    }
//}
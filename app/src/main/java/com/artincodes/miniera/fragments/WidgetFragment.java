package com.artincodes.miniera.fragments;


import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.widgetutils.WidgetDBHelper;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class WidgetFragment extends Fragment {


    public WidgetFragment() {
    }

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private NestedScrollView nestedScrollView;
    private WidgetDBHelper widgetDBHelper;
    AppWidgetHostView hostView;


    ViewGroup mainlayout;
    int count = 0;

    public static final WidgetFragment newInstance()

    {

        WidgetFragment fragment = new WidgetFragment();

        Bundle bdl = new Bundle(1);

//        bdl.putString(EXTRA_MESSAGE, message);

        fragment.setArguments(bdl);

        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_widget, container, false);
        FloatingActionButton addWidgetButton = (FloatingActionButton) rootView.findViewById(R.id.add_widget_button);
        Button clearDBButton = (Button) rootView.findViewById(R.id.clear_db);
//        nestedScrollView = (NestedScrollView)rootView.findViewById(R.id.nestedScrollView);
        mainlayout = (ViewGroup) rootView.findViewById(R.id.widget_layout);
        widgetDBHelper = new WidgetDBHelper(getActivity());
//        widgetDBHelper.clearDb();

//        makeMyScrollSmart();


        mAppWidgetManager = AppWidgetManager.getInstance(getActivity());
        mAppWidgetHost = new AppWidgetHost(getActivity(), R.id.APPWIDGET_HOST_ID);


        addWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWidget();
            }
        });


        clearDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetDBHelper.clearDb();
            }
        });
//        new Thread(new Runnable() {
//            public void run() {
//                Toast.makeText(getActivity(),"DB Cleared",Toast.LENGTH_SHORT).show();
//            }
//        }).start();

        new LoadWidgets().execute();

        return rootView;
    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    /**
     * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
     * which is used to select widgets. This just adds empty extras to the
     * intent, avoiding the bug.
     * <p/>
     * See more: http://code.google.com/p/android/issues/detail?id=4272
     */


    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }


    /**
     * If the user has selected an widget, the result will be in the 'data' when
     * this function is called.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                Log.i("Widget", data.getDataString() + "");
                configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
                Log.i("Widget  CREATE", data.getDataString() + "");

            }
        } else if (resultCode == getActivity().RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
            Log.i("Widget  cancelled", data.getDataString() + "");
        }
    }


    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        dumpIntent(data);
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }


    public void createWidget(Intent data) {

        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        setupWidget(appWidgetId);
        widgetDBHelper.insertWidget(count, appWidgetId);

    }

    public void setupWidget(int widgetId) {

        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(widgetId);
        String label = appWidgetInfo.label;
//        appWidgetInfo.configure;

        Log.i("WIDGET LABEL", label);


        hostView = mAppWidgetHost.createView(getActivity(), widgetId, appWidgetInfo);
        hostView.setAppWidget(widgetId, appWidgetInfo);
//        hostView.setMinimumHeight(300);
        if (label.equals("Gmail")) {
            hostView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            300));
        } else {
            hostView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

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
        mainlayout.addView(hostView);

    }

    /**
     * Registers the AppWidgetHost to listen for updates to any widgets this app
     * has.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }


    /**
     * Stop listen for updates for our widgets (saving battery).
     */
    @Override
    public void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }


    /**
     * Removes the widget displayed by this AppWidgetHostView.
     */
    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        mainlayout.removeView(hostView);
    }


    private void makeMyScrollSmart() {
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View __v, MotionEvent __event) {
                if (__event.getAction() == MotionEvent.ACTION_DOWN) {
                    //  Disallow the touch request for parent scroll on touch of child view
                    requestDisallowParentInterceptTouchEvent(__v, true);
                } else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Re-allows parent events
                    requestDisallowParentInterceptTouchEvent(__v, false);
                }
                return false;
            }
        });
    }

    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof NestedScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
    }


    public static void dumpIntent(Intent i) {

        String LOG_TAG = "INTENT EXTRA";

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e(LOG_TAG, "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(LOG_TAG, "[" + key + "=" + bundle.get(key) + "]");
            }
            Log.e(LOG_TAG, "Dumping Intent end");
        }
    }


    public void loadWidgets() {


    }

    public class LoadWidgets extends AsyncTask<Void, Void, Void> {

        int[] widgetId;

        @Override
        protected Void doInBackground(Void... params) {

//            loadWidgets();

            Cursor cursor = widgetDBHelper.getWidgets();
            cursor.moveToFirst();
            int i = 0;

            widgetId = new int[cursor.getCount()];
            count = cursor.getCount();
            while (!cursor.isAfterLast()) {
                widgetId[i] = Integer.parseInt(cursor.getString(1));
                cursor.moveToNext();
                i++;
            }

            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            for (int i = 0; i < widgetId.length; i++)

                setupWidget(widgetId[i]);

        }

    }


}


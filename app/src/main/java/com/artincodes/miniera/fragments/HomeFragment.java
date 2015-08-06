package com.artincodes.miniera.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.HomeViewPagerAdapter;
import com.artincodes.miniera.utils.dock.DockAppsDBHelper;
import com.artincodes.miniera.utils.dock.ImageAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * A simple {@link Fragment} subclass.
 */


public class HomeFragment extends Fragment {

    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("h:mm,EEEEEEEEE,d MMMMMMMMM", Locale.US);
    public static TextView timeTextView;
    public static TextView dateTextView;
    public static Typeface robotoCond;
    BroadcastReceiver timeBroadcastReciever;
    static GridView dockGrid;
    static Context c;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        dockGrid = (GridView) rootView.findViewById(R.id.dock_grid);

        timeTextView = (TextView) rootView.findViewById(R.id.text_time);
        dateTextView = (TextView) rootView.findViewById(R.id.text_date);
        robotoCond = Typeface.createFromAsset(getActivity().getBaseContext().getAssets(), "roboto_thin.ttf");


        c = getActivity();


        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                return false;
            }
        });
        timeTextView.setTypeface(robotoCond);
        timeTextView.setShadowLayer(2, 0, 1, Color.BLACK);
        dateTextView.setShadowLayer(2, 0, 1, Color.BLACK);

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setDateTime();

        timeBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    setDateTime();

                }
            }
        };

        getActivity().registerReceiver(timeBroadcastReciever, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void setDateTime() {

        String dateTime = _sdfWatchTime.format(new Date());
        String[] parts = dateTime.split(",");
        timeTextView.setText(parts[0]);
        dateTextView.setText(parts[2] + ", " + parts[1]);
//        textDay.setText(parts[1]);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (timeBroadcastReciever != null)
            getActivity().unregisterReceiver(timeBroadcastReciever);
    }


    public static void updateDock() {

        final DockAppsDBHelper dockAppsDBHelper = new DockAppsDBHelper(c);
        try {

            Cursor cursor = dockAppsDBHelper.getDockApps();
            String TAG = "UPDATE DOCK";
            final String[] packageNames = new String[5];
            final String[] labels = new String[5];
            Drawable[] icons = new Drawable[5];
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int index = cursor.getInt(0);
                labels[index] = cursor.getString(2);
                if (labels[index].equals("Phone")) {
                    packageNames[index] = "com.android.phone";
                } else {
                    packageNames[index] = cursor.getString(1);
                }

                try {
                    icons[index] = MainActivity.packageManager.getApplicationIcon(packageNames[index]);
                } catch (PackageManager.NameNotFoundException NNFE) {

                } catch (NullPointerException NPE) {

                }

                Log.i(TAG, packageNames[index]);
                cursor.moveToNext();

            }

            dockGrid.setAdapter(new ImageAdapter(c, packageNames, icons));
            dockGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openApp(packageNames[position]);
                }
            });
            dockGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    final MaterialDialog DeleteAppDialog = new MaterialDialog(c);
                    DeleteAppDialog.setTitle(labels[position])
                            .setMessage("Do you want to remove "+labels[position]+"from dock?")
                            .setPositiveButton("DELETE", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dockAppsDBHelper.deleteApp(position);
                                    DeleteAppDialog.dismiss();
                                    updateDock();

                                }
                            })
                            .setNegativeButton("CANCEL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DeleteAppDialog.dismiss();
                                }
                            })
                            .show();

                    return false;
                }
            });

        }catch (NullPointerException e){

        }

    }


    private static void openApp(String packageName) {

        Intent launchIntent=null;

        if (packageName.equals("com.android.phone")){
            launchIntent = new Intent(Intent.ACTION_DIAL);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else {
            launchIntent = MainActivity.packageManager.getLaunchIntentForPackage(packageName);
        }
        c.startActivity(launchIntent);

    }

}




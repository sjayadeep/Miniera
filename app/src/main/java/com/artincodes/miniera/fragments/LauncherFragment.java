package com.artincodes.miniera.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.AppPack;
import com.artincodes.miniera.utils.launcher.AppsDBHelper;
import com.artincodes.miniera.utils.launcher.DrawerAdapter;
import com.artincodes.miniera.utils.launcher.DrawerClickListener;
import com.artincodes.miniera.utils.launcher.DrawerLongClickListener;
import com.artincodes.miniera.utils.launcher.StaticGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment {


    //    public static GridView drawerGrid_old;
    DrawerAdapter drawerAdapterObject;
    String[] labels;
    String[] packageNames;
    Drawable[] icons;

    List<AppPack> appList = new ArrayList<>();

//    Map<String, List<String>> mapLetterApp = new HashMap<String, List<String>>();
//
//    // create list one and store values
//    List<String> appSet = new ArrayList<String>();

    public static Map<String, Integer> mapIndex;

    String TAG = "LAUNCHER FRAGMENT";
    ViewGroup drawerLayout;

    NestedScrollView scrollView;

    View rootView;


    public LauncherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_launcher, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.apps_toolbar);
//        getActivity().setActionBar(toolbar);

//        MainActivity.se
        toolbar.setTitle("Apps");
        scrollView = (NestedScrollView) rootView.findViewById(R.id.scrollView);

                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        drawerLayout = (ViewGroup) rootView.findViewById(R.id.drawer_layout);
//        drawerGrid_old = (GridView) rootView.findViewById(R.id.drawer_grid);

        new SetDrawer().execute();

        return rootView;
    }


    private void displayIndex() {

        LinearLayout indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setShadowLayer(2, 0, 1, Color.BLACK);
            textView.setText(index.toUpperCase());
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    TextView selectedIndex = (TextView) v;
//                    drawerLayout.setSelected(6);
//                    drawerGrid_old.setSelection(mapIndex.get(selectedIndex.getText().toString().toLowerCase()));
                    return false;
                }
            });

            indexLayout.addView(textView);
        }


    }

//    public void setMapIndex() {
//
//
//
//
//        displayIndex();
//    }

    private class SetDrawer extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            Log.i("DRAWER", "DO IN BG IS CALLED");

            if (drawerAdapterObject == null) {
                Log.i("DRAWER", "INSIDE CONDITION");
                AppsDBHelper AppsDB = new AppsDBHelper(getActivity());
                Cursor c = AppsDB.getApps();
                labels = new String[c.getCount()];
                packageNames = new String[c.getCount()];
                icons = new Drawable[c.getCount()];

                c.moveToFirst();
                int i = 0;


                while (!c.isAfterLast()) {


                    labels[i] = c.getString(2);
                    packageNames[i] = c.getString(1);
                    try {
                        icons[i] = MainActivity.packageManager.getApplicationIcon(packageNames[i]);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.i("PACKAGE", packageNames[i]);
                    }


                    i++;
                    c.moveToNext();

                }
                c.close();
            }


////            String letter = "a";
//            for (int i = 0; i < labels.length; i++) {
//
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            String index = null;

            mapIndex = new LinkedHashMap<String, Integer>();
            mapIndex.clear();

            for (int i = 0; i < labels.length; i++) {
                index = labels[i].substring(0, 1);

                if (mapIndex.get(index.toLowerCase()) == null) {
                    if (i != 0) {

                        addGridViewApps(appList);
                        appList.clear();

//                        break;
                    }
                    mapIndex.put(index.toLowerCase(), i);

//                if(i!=0){
//                    letter=index;
//
//                }
                    Log.i(TAG, "Inside condition" + labels[i]);
                } else {

                    Log.i(TAG, labels[i]);

                }

                appList.add(new AppPack(labels[i], packageNames[i], icons[i]));
            }

            addGridViewApps(appList);
            appList.clear();

            Log.i("DRAWER", "POST EXECUTE IS CALLED");

//            drawerGrid_old.setAdapter(drawerAdapterObject);
//            drawerGrid_old.setOnItemClickListener(new DrawerClickListener(getActivity(),
//                    packageNames, labels, MainActivity.packageManager));
//            drawerGrid_old.setOnItemLongClickListener(new DrawerLongClickListener(getActivity(),
//                    packageNames, labels, icons, MainActivity.packageManager));

            displayIndex();

        }


    }


    public void addGridViewApps(List<AppPack> appList) {

        List<AppPack> apps = new ArrayList<>(appList);
        StaticGridView drawerGrid = new StaticGridView(getActivity());
        TextView indexText = new TextView(getActivity());
        indexText.setText(apps.get(0).label.substring(0,1).toUpperCase());
        indexText.setTextColor(Color.WHITE);
        indexText.setTextSize(22);
//        if (drawerAdapterObject == null)
        drawerAdapterObject = new DrawerAdapter(getActivity(), labels, apps);
        drawerGrid.setScrollContainer(false);
        drawerGrid.setAdapter(drawerAdapterObject);
//        drawerGrid.onMe
        int height = 0;
        if (apps.size() < 5) {
            height = ((apps.size() / 4) * 90) + 180;
        } else {
            height = ((apps.size() / 4) * 90) + 200;
        }
        Log.i(TAG, "Height : " + height);
        drawerGrid.setNumColumns(4);
//        drawerGrid.
        LinearLayout.LayoutParams lpTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lpTextView.setMargins(50, 0, 0, 30);
        indexText.setLayoutParams(lpTextView);

        drawerLayout.addView(indexText);

//        lp.getMarginEnd().
        LinearLayout.LayoutParams lpGrid = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lpGrid.setMargins(0, 0, 0, 30);

        drawerGrid.setLayoutParams(lpGrid);

        drawerLayout.addView(drawerGrid);
    }

}





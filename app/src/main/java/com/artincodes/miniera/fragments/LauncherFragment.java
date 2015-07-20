package com.artincodes.miniera.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.artincodes.miniera.utils.launcher.AppsDBHelper;
import com.artincodes.miniera.utils.launcher.DrawerAdapter;
import com.artincodes.miniera.utils.launcher.DrawerClickListener;
import com.artincodes.miniera.utils.launcher.DrawerLongClickListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment {


    public static GridView drawerGrid;
    DrawerAdapter drawerAdapterObject;
    String[] labels;
    String[] packageNames;
    Drawable[] icons;
    public static Map<String, Integer> mapIndex;



    View rootView;


    public LauncherFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_launcher, container, false);

        drawerGrid = (GridView) rootView.findViewById(R.id.drawer_grid);

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
                    drawerGrid.setSelection(mapIndex.get(selectedIndex.getText().toString().toLowerCase()));
                    return false;
                }
            });

            indexLayout.addView(textView);
        }



    }

    public void setMapIndex(){

        mapIndex = new LinkedHashMap<String, Integer>();
        mapIndex.clear();
        String index;
        for (int i=0;i<labels.length;i++) {
            index = labels[i].substring(0, 1);

            if (mapIndex.get(index.toLowerCase()) == null)
                mapIndex.put(index.toLowerCase(), i);
        }
        displayIndex();
    }

    private class SetDrawer extends AsyncTask<Void,Void,Void>{



        @Override
        protected Void doInBackground(Void... params) {

            Log.i("DRAWER","DO IN BG IS CALLED");

            if (drawerAdapterObject == null){
                Log.i("DRAWER","INSIDE CONDITION");
                AppsDBHelper AppsDB = new AppsDBHelper(getActivity());
                Cursor c = AppsDB.getApps();
                labels = new String[c.getCount()];
                packageNames = new String[c.getCount()];
                icons = new Drawable[c.getCount()];

                c.moveToFirst();
                int i=0;

                while (!c.isAfterLast()){

                    labels[i] = c.getString(2);
                    packageNames[i]=c.getString(1);
                    try {
                        icons[i] = MainActivity.packageManager.getApplicationIcon(packageNames[i]);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.i("PACKAGE", packageNames[i]);
                    }

                    i++;
                    c.moveToNext();

                }
                c.moveToLast();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            if (drawerAdapterObject==null)
                drawerAdapterObject = new DrawerAdapter(getActivity(),labels,packageNames, icons);
            Log.i("DRAWER","POST EXECUTE IS CALLED");

            drawerGrid.setAdapter(drawerAdapterObject);
            drawerGrid.setOnItemClickListener(new DrawerClickListener(getActivity(),
                    packageNames, labels, MainActivity.packageManager));
            drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(getActivity(),
                    packageNames,labels,icons,MainActivity.packageManager));

            setMapIndex();

        }


    }

}





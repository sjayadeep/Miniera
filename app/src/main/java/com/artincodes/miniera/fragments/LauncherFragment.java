package com.artincodes.miniera.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.AppPack;
import com.artincodes.miniera.utils.launcher.AppsDBHelper;
import com.artincodes.miniera.utils.launcher.AppsRecyclerViewAdapter;
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


    String[] labels;
    String[] packageNames;
    Drawable[] icons;
    public static boolean appLaunchable = true;

    List<AppPack> appList = new ArrayList<>();

    List<List<AppPack>> fullAppsList = new ArrayList<>();


    public static Map<String, Integer> mapIndex;

    String TAG = "LAUNCHER FRAGMENT";

    NestedScrollView scrollView;

    View rootView;
    AppsRecyclerViewAdapter appsRecyclerViewAdapter;
    RecyclerView appsRecyclerView;
    AppsDBHelper AppsDB;

    GridView searchGridView;
    SearchView appsSearchView;
    TextView searchTextTitle;
    LinearLayout indexLayout;

    int count = 0;
    boolean adapterSet = false;


    public LauncherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_launcher, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.apps_toolbar);
//        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setContentInsetsAbsolute(0, 0);
        searchGridView = (GridView) rootView.findViewById(R.id.search_grid);
        searchGridView.setVisibility(View.GONE);
        appsSearchView = (SearchView) rootView.findViewById(R.id.app_search_view);
        appsRecyclerView = (RecyclerView) rootView.findViewById(R.id.apps_recycler_view);
        appsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        appsRecyclerView.setLayoutManager(llm);
        AppsDB = new AppsDBHelper(getActivity());
        scrollView = (NestedScrollView) rootView.findViewById(R.id.scrollView);
        searchTextTitle = (TextView) rootView.findViewById(R.id.search_text_title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        new SetDrawer().execute();

        appsSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    appsRecyclerView.setVisibility(View.GONE);
                    searchTextTitle.setVisibility(View.GONE);
                    indexLayout.setVisibility(View.GONE);

                    searchGridView.setVisibility(View.VISIBLE);
                } else {

                }
            }
        });

//        appsSearchView.setc

        appsSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                clearSearchView();

                return true;
            }
        });

//        appsSearchView.setOnClickListener();

        appsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                Cursor seachCursor = AppsDB.searchApps(query);
                seachCursor.moveToFirst();


                String[] searchLabels = new String[seachCursor.getCount()];
                String[] searchPackageNames = new String[seachCursor.getCount()];
                Drawable[] searchIcons = new Drawable[seachCursor.getCount()];
                List<AppPack> appSearchList = new ArrayList<>();

                seachCursor.moveToFirst();
                int i = 0;


                while (!seachCursor.isAfterLast()) {


                    searchLabels[i] = seachCursor.getString(2);
                    searchPackageNames[i] = seachCursor.getString(1);
                    try {
                        if (searchLabels[i].equals("Phone")) {
                            searchIcons[i] = MainActivity.packageManager.getApplicationIcon("com.android.phone");
                        } else
                            searchIcons[i] = MainActivity.packageManager.getApplicationIcon(searchPackageNames[i]);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.i("PACKAGE", searchPackageNames[i]);
                    }

                    appSearchList.add(new AppPack(searchLabels[i], searchPackageNames[i], searchIcons[i]));

                    i++;
                    seachCursor.moveToNext();

                }
                seachCursor.close();


//                Log.i(TAG, "Result Count" + seachCursor.getCount());

                searchGridView.setAdapter(new DrawerAdapter(getActivity(), appSearchList));
                searchGridView.setOnItemClickListener(new DrawerClickListener(getActivity(), appSearchList));
                searchGridView.setOnItemLongClickListener(new DrawerLongClickListener(getActivity(), appSearchList));

                return false;
            }
        });

        appsSearchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //check if the right key was pressed
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
//                                Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(edtTransactionDesc.getWindowToken(), 0);
//                        AddTransactionView.this.requestFocus();
                        appsSearchView.setIconified(true);
                        return false;
                    }
                }
                return true;
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(new LauncherPacReceiver(), intentFilter);

        return rootView;
    }


    private void displayIndex() {

        indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);
        indexLayout.removeAllViews();

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (final String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
//            textView.setShadowLayer(2, 0, 1, Color.BLACK);
            textView.setText(index.toUpperCase());
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    TextView selectedIndex = (TextView) v;
                    Log.i(TAG, "Scrolling to : " + mapIndex.get(selectedIndex.getText().toString().toLowerCase()));
                    appsRecyclerView.scrollToPosition(mapIndex.get(selectedIndex.getText().toString().toLowerCase()));

                    return false;
                }
            });

            indexLayout.addView(textView);
        }


    }


    private class SetDrawer extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            Log.i("DRAWER", "DO IN BG IS CALLED");
            try {
                mapIndex.clear();

            }catch (NullPointerException e){
                //TODO
            }

            if (true) {
                adapterSet = false;
                fullAppsList.clear();
                Log.i("DRAWER", "INSIDE CONDITION");

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
                        if (labels[i].equals("Phone")) {
                            icons[i] = MainActivity.packageManager.getApplicationIcon("com.android.phone");
                        } else
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

            String index = null;

            mapIndex = new LinkedHashMap<String, Integer>();
            mapIndex.clear();
            count = 0;


            for (int i = 0; i < labels.length; i++) {
                index = labels[i].substring(0, 1);


                if (mapIndex.get(index.toLowerCase()) == null) {
                    if (i != 0) {

                        addGridViewApps(appList);
                        appList.clear();
                    }
                    mapIndex.put(index.toLowerCase(), count++);
//                    count=count+1;

                } else {
                }

                appList.add(new AppPack(labels[i], packageNames[i], icons[i]));
            }

            addGridViewApps(appList);
            appList.clear();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            displayIndex();
            Log.i(TAG, "Calling Adapter");
            appsRecyclerViewAdapter = null;
//            if (adapterSet == false) {
            appsRecyclerViewAdapter = new AppsRecyclerViewAdapter(getActivity(), fullAppsList);
            adapterSet = true;
//            }

            appsRecyclerView.setAdapter(appsRecyclerViewAdapter);


        }


    }


    public void addGridViewApps(List<AppPack> appList) {

        List<AppPack> apps = new ArrayList<>(appList);
        fullAppsList.add(apps);

    }

    @Override
    public void onPause() {
        clearSearchView();

        super.onPause();
    }


    public void clearSearchView() {
        appsRecyclerView.setVisibility(View.VISIBLE);
        searchTextTitle.setVisibility(View.VISIBLE);
        try {
            indexLayout.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {

        }

        searchGridView.setVisibility(View.GONE);
        appsSearchView.onActionViewCollapsed();

    }



    public class LauncherPacReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.i("PAC RECIEVER", "PACKAGE CHANGED");

            new SetDrawer().execute();
        }

    }

}





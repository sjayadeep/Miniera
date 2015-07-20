package com.artincodes.miniera.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.artincodes.miniera.utils.recent.ExpandableListAdapter;
import com.artincodes.miniera.utils.recent.ExpandableListDataPump;
import com.artincodes.miniera.utils.recent.NotificationAccessibilityService.Constants;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.recent.RecentDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    RecentDBHelper recentDBHelper;

    String TAG = "NOTIFICATION";

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;


    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        final IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_CATCH_NOTIFICATION);

        getActivity().registerReceiver(NotificationCatcherReceiver, mIntentFilter);

        View rootView =  inflater.inflate(R.layout.fragment_recent, container, false);

        recentDBHelper= new RecentDBHelper(getActivity());

        Cursor cursor = recentDBHelper.getWhatsappRecentItems();
        Toast.makeText(getActivity(),cursor.getCount()+"",Toast.LENGTH_SHORT).show();
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        ExpandableListDataPump expandableListDataPump = new ExpandableListDataPump(getActivity());

        expandableListDetail = expandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                recentDBHelper.clearRecent();
                Log.i("DB","Cleared");
//
            }
        });
//
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//
//
//            }
//        });
//
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getActivity(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                )
//                        .show();
//                return false;
//            }
//        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(NotificationCatcherReceiver);
    }

    private final BroadcastReceiver NotificationCatcherReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra(Constants.EXTRA_PACKAGE);
            String message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
            Log.v(TAG, packageName);
            Log.v(TAG, message);
//            Toast.makeText(getActivity(),"NEW NOTIFICATION",Toast.LENGTH_SHORT).show();

            if (packageName.equals("com.whatsapp")) {
                recentDBHelper.insertItem(packageName, message);

                Cursor cursor = recentDBHelper.getWhatsappRecentItems();
                cursor.moveToFirst();

                while (!cursor.isAfterLast()){
                    Log.i("DB",cursor.getString(0)+", " +cursor.getString(1));
                    cursor.moveToNext();
                }
            }
        }
    };

}

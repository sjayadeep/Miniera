package com.artincodes.miniera.utils.launcher;

/**
 * Created by jayadeep on 24/12/14.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.fragments.LauncherFragment;

public class DrawerClickListener implements OnItemClickListener {

    Context mContext;
    String[] packageNames;
    String[] labels;
    PackageManager pmForListener;

    public DrawerClickListener(Context c, String[] packageNames, String[] labels,PackageManager pm) {
        mContext = c;
        this.packageNames = packageNames;
        this.labels = labels;
        pmForListener = pm;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        // TODO Auto-generated method stub
//        if (MainActivity.appLaunchable) {

            Intent launchIntent = null;

            if (labels[pos].equals("Phone")){
                launchIntent = new Intent(Intent.ACTION_DIAL);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else {
                launchIntent = pmForListener.getLaunchIntentForPackage(packageNames[pos]);
            }
            mContext.startActivity(launchIntent);

        }


//    }


}
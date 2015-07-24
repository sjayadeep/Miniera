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
import com.artincodes.miniera.utils.AppPack;

import java.util.List;

public class DrawerClickListener implements OnItemClickListener {

    Context mContext;
//    String[] packageNames;
//    String[] labels;
    PackageManager pmForListener = MainActivity.packageManager;
    List<AppPack> appPackList;

    public DrawerClickListener(Context c, List<AppPack> appPackList) {
        mContext = c;
        this.appPackList = appPackList;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        // TODO Auto-generated method stub
//        if (MainActivity.appLaunchable) {

            Intent launchIntent = null;

        String label = appPackList.get(pos).label;

            if (label.equals("Phone")){
                launchIntent = new Intent(Intent.ACTION_DIAL);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else {
                launchIntent = pmForListener.getLaunchIntentForPackage(appPackList.get(pos).package_name);
            }
            mContext.startActivity(launchIntent);

        }


//    }


}
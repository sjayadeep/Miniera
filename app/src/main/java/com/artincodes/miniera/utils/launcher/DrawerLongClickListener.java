package com.artincodes.miniera.utils.launcher;

/**
 * Created by jayadeep on 24/12/14.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.fragments.LauncherFragment;
import com.artincodes.miniera.fragments.MiniAppdrawerFragment;
import com.artincodes.miniera.utils.AppPack;
import com.artincodes.miniera.utils.ListAdapterCommon;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class DrawerLongClickListener implements OnItemLongClickListener {

    Context mContext;
    //    Pac[] pacsForAdapter;
//    String[] packageNames;
//    String[] labels;
//    Drawable[] icons;

    String TAG = "DrawerLongClickListener";

    List<AppPack> appPackList;

    PackageManager pmForListener = MainActivity.packageManager;
    MaterialDialog appOptionsDialog = null;
    View convertView;
    ImageView appIcon;
    TextView appName;
    ImageView moreButton;
    Vibrator mVibrator;
//    MaterialDialog moreDialog;

    String[] optionTitles = {
            "Uninstall",
            "App info",
            "View in Playstore",
    };

    Integer[] optionIcons = {
            R.mipmap.ic_delete,
            R.mipmap.ic_info,
            R.mipmap.ic_play_store,
    };


    ListView listViewOptions;
    ListAdapterCommon listAdapterCommon;


    int index;
    String packageName;


    public DrawerLongClickListener(Context c,List<AppPack> appPackList
                                   ) {
        mContext = c;
//        pacsForAdapter = pacs;
        this.appPackList = appPackList;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


//        MainActivity.appLaunchable = false;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.app_options_dialog_conent, null);
//
//        moreOptionView = layoutInflater.inflate(R.layout.more_dialog_content, null);


        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(50);
        Log.i(TAG,"Long click listener");
//        Toast.makeText(mContext,pacsForAdapter[position].label+"\n"+
//                MiniAppdrawerFragment.pacsForMiniAdapter.length,Toast.LENGTH_SHORT).show();


//        MiniAppdrawerFragment.pacsForMiniAdapter[MiniAppdrawerFragment.pacsForMiniAdapter.length] = pacsForAdapter[position];
        ImageView imageView = new ImageView(mContext);
        Cursor c = MainActivity.miniDrawerAppsDBHelper.getMiniDrawerApps();
        index = c.getCount();
        packageName = appPackList.get(position).package_name;
//        Toast.makeText(mContext,packageName+"",Toast.LENGTH_SHORT).show();

//        imageView.setImageDrawable(pacsForAdapter[position].icon);

//        MiniAppdrawerFragment.minidrawerGrid.addView(imageView);

        appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
        appName = (TextView) convertView.findViewById(R.id.text_app_name);
//        moreButton = (ImageView) convertView.findViewById(R.id.more_icon);

        appIcon.setImageDrawable(appPackList.get(position).icon);
        appName.setText(appPackList.get(position).label);

        listAdapterCommon = new ListAdapterCommon(mContext, optionTitles, optionIcons, "#000000");
        listViewOptions = (ListView) convertView.findViewById(R.id.more_options_list);
        listViewOptions.setAdapter(listAdapterCommon);


        appOptionsDialog = new MaterialDialog(mContext)
//                .setTitle("Add to Home Screen?")
//                        .setMessage("Select room")
                .setContentView(convertView)
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appOptionsDialog.dismiss();
//                        setRoomSelectedFalse();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .setPositiveButton("PIN IT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(mContext, "Negative", Toast.LENGTH_SHORT).show();

                        MainActivity.miniDrawerAppsDBHelper.insertApp(index,
                                packageName,
                                appPackList.get(position).label);
                        appOptionsDialog.dismiss();


                    }
                });

        listViewOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0:
                        //Toast.makeText(mContext,"Uninstall "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
                        Uri packageUri = Uri.parse("package:" + appPackList.get(position).package_name);
                        Intent uninstallIntent =
                                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                        mContext.startActivity(uninstallIntent);
                        appOptionsDialog.dismiss();

                        break;
                    case 1:
                        //Toast.makeText(mContext,"Info "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + appPackList.get(position).package_name));
                        mContext.startActivity(intent);
                        appOptionsDialog.dismiss();

                        break;
                    case 2:
                        //Toast.makeText(mContext,"Playstore "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackList.get(position).package_name)));

                        appOptionsDialog.dismiss();
                        break;
                    default:
                        break;

                }

            }
        });


        appOptionsDialog.show();
//        room1 = (TextView) convertView.findViewById(R.id.room1);
//        room2 = (TextView) convertView.findViewById(R.id.room2);
//        room3 = (TextView) convertView.findViewById(R.id.room3);
//        room4 = (TextView) convertView.findViewById(R.id.room4);
//
//        room1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedRoom = 0;
//                setRoomSelectedTrue();
//                resetLabelBackground();
//                v.setBackgroundResource(R.drawable.border_selected);
//
//            }
//        });
//        room2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedRoom = 1;
//                setRoomSelectedTrue();
//                resetLabelBackground();
//                v.setBackgroundResource(R.drawable.border_selected);
//
//            }
//        });
//        room3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedRoom = 3;
//                setRoomSelectedTrue();
//                resetLabelBackground();
//                v.setBackgroundResource(R.drawable.border_selected);
//            }
//        });
//        room4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedRoom=4;
//                setRoomSelectedTrue();
//                resetLabelBackground();
//                v.setBackgroundResource(R.drawable.border_selected);
//            }
//        });
//

//
//        mMaterialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                LauncherFragment.appLaunchable = true;
//
//            }
//        });
//        mMaterialDialog.show();
//
//
//        MainActivity.dragIcon.setImageDrawable(pacsForAdapter[position].icon);
//        MainActivity.dockGrid.setVisibility(View.VISIBLE);
//
//        moreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMaterialDialog.dismiss();

//
//                moreDialog = new MaterialDialog(mContext)
//                        .setTitle("More")
//                                .setMessage("More options")
//                        .setContentView(moreOptionView)
//                        .setPositiveButton("CANCEL", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                moreDialog.dismiss();
//                            }
//                        });
//
//                listViewOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//                        switch (pos) {
//                            case 0:
//                                Toast.makeText(mContext,"Uninstall "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
//                                Uri packageUri = Uri.parse("package:" + pacsForAdapter[position].name);
//                                Intent uninstallIntent =
//                                        new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
//                                mContext.startActivity(uninstallIntent);
//                                moreDialog.dismiss();
//
//                                break;
//                            case 1:
//                                Toast.makeText(mContext,"Info "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                intent.setData(Uri.parse("package:" + pacsForAdapter[position].name));
//                                mContext.startActivity(intent);
//                                moreDialog.dismiss();
//
//                                break;
//                            case 2:
//                                Toast.makeText(mContext,"Playstore "+pacsForAdapter[position].label,Toast.LENGTH_SHORT).show();
//                                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("market://details?id=" + pacsForAdapter[position].name)));
//
//                                moreDialog.dismiss();
//                                break;
//                            default:
//                                break;
//
//                        }
//
//                    }
//                });
//                moreDialog.show();
//
//            }
//        });
//
//        return false;
//    }
//
//    private void resetLabelBackground() {
//        room1.setBackgroundResource(R.drawable.border);
//        room2.setBackgroundResource(R.drawable.border);
//        room3.setBackgroundResource(R.drawable.border);
//        room4.setBackgroundResource(R.drawable.border);
//    }
//
//    private void setRoomSelectedTrue() {
//
//        roomSelected = true;
//
//    }
//
//    private void setRoomSelectedFalse() {
//
//        roomSelected = false;
        return true;
    }
}
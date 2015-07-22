package com.artincodes.miniera.utils.launcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.fragments.LauncherFragment;
import com.artincodes.miniera.utils.AppPack;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {

    Context mContext;
//    Pac[] pacsForAdapter;
    String[] labels;
//    String [] packageNames;
//    Drawable[] icons;
    List<AppPack> appPackList;
//    String mSource;

    //AlphaAnimation animation1;


    public DrawerAdapter (Context c, String[] labels,List<AppPack> appPackList){

        mContext = c;
        this.labels = labels;
        this.appPackList = appPackList;
//        this.icons = icons;
//        mSource = source;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub


        return appPackList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    static class ViewHolder{
        TextView text;
        ImageView icon;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.drawer_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView)convertView.findViewById(R.id.icon_text);
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.icon_image);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }



//        try {

//            viewHolder.icon.setImageDrawable(
//                    MainActivity.packageManager.getApplicationIcon(packageNames[pos]));
//        try {
//            viewHolder.icon.setImageDrawable(MainActivity.packageManager.getApplicationIcon(packageNames[pos]));
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }catch (NullPointerException e){
//            Log.i("PACKAGE",packageNames[pos]);
//        }
        viewHolder.icon.setImageDrawable(appPackList.get(pos).icon);
        viewHolder.text.setText(appPackList.get(pos).label);


//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }


//        viewHolder.icon.setImageDrawable(pacsForAdapter[pos].icon);
//        if (mSource.equals("mini")){
//            viewHolder.text.setVisibility(View.GONE);
//        }else {
//        }

        //animation1 = new AlphaAnimation(0.0f, 1.0f);
        //animation1.setDuration(500);

        //convertView.startAnimation(animation1);
        convertView.animate().setDuration(200).alphaBy(1);
        return convertView;
    }




}
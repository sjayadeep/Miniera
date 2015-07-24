package com.artincodes.miniera.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.AppPack;
import com.artincodes.miniera.utils.DraggableGrid.DynamicGridAdapter;
import com.artincodes.miniera.utils.DraggableGrid.DynamicGridView;
import com.artincodes.miniera.utils.launcher.DrawerClickListener;
import com.artincodes.miniera.utils.reveal.RevealLayout;
import com.artincodes.miniera.utils.launcher.DrawerAdapter;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */



public class MiniAppdrawerFragment extends Fragment {


    public static RevealLayout mRevealLayout;

//    public Pac[] pacsForMiniAdapter;
    String[] labels;
    String[] packageNames;
    Drawable[] icons;
    boolean appsDeletable = false;
    ImageView deleteAppButton;

    List<AppPack> appList = new ArrayList<>();

    DrawerAdapter miniDrawerAdapter;
    //    public static GridView minidrawerGrid;
//    public static GridView minidrawerGrid;
    public static DynamicGridView minidrawerGrid;
    TestAdapter adapter;

    public MiniAppdrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mini_appdrawer, container, false);
        mRevealLayout = (RevealLayout) rootView.findViewById(R.id.reveal_layout);
        mRevealLayout.setContentShown(false);
        minidrawerGrid = (DynamicGridView)rootView.findViewById(R.id.minidrawer_grid);
        deleteAppButton = (ImageView)rootView.findViewById(R.id.deleteAppButton);
//        final LayoutAnimationController wobble = AnimationUtils.loadLayoutAnimation(getActivity(),R.anim.grid_wobble_animation);

        adapter = new TestAdapter();


//        for (int i = 0 ; i < 10 ; i++){
//            adapter.add(i+""+i);
//        }

//        final Vibrator mVibrator= (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);


        Cursor c = MainActivity.miniDrawerAppsDBHelper.getMiniDrawerApps();
        int size = c.getCount();

        labels = new String[size];
        packageNames = new String[size];
        icons = new Drawable[size];


        c.moveToFirst();
        int i=0;

//        pacsForMiniAdapter[0].label="Hello";
//        Drawable icon;


        while (!c.isAfterLast()) {

//            pacsForMiniAdapter[i] = new Pac();

            labels[i]=c.getString(2);
            packageNames[i] = c.getString(1);

            try {
                if (labels[i].equals("Phone")) {
                    icons[i] = MainActivity.packageManager.getApplicationIcon("com.android.phone");
                }else
                    icons[i] = MainActivity.packageManager.getApplicationIcon(packageNames[i]);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            adapter.add(packageNames+"");
//            adapter.add(pacsForMiniAdapter[i].icon);
            appList.add(new AppPack(labels[i], packageNames[i], icons[i]));
            i++;
            c.moveToNext();

        }

//        minidrawerGrid.setAdapter(new DrawerAdapter(getActivity(),pacsForMiniAdapter,"mini"));

//        DrawerAdapter drawerAdapter = new DrawerAdapter(getActivity(),pacsForMiniAdapter,"mini");


        minidrawerGrid.setAdapter(adapter);
        setAppOpenListener();
//        minidrawerGrid.setOnItemClickListener(new DrawerClickListener(getActivity(),
//                pacsForMiniAdapter, MainActivity.packageManager));


        deleteAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appsDeletable){
                    setDeleteListener();
//                    minidrawerGrid.setAnimation(wobble);
//                    minidrawerGrid.setLayoutAnimation(wobble);
//                    minidrawerGrid.startLayoutAnimation();
                    appsDeletable=true;

                }else {
                    appsDeletable=false;
                    setAppOpenListener();
//                    minidrawerGrid.clearAnimation();


                }
            }
        });


        mRevealLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRevealLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRevealLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int x = (mRevealLayout.getWidth()/2 );
                        int y = mRevealLayout.getHeight()-50;
                        //TODO ERROR CORRECTION
                        try {
                            mRevealLayout.show(x,y,200);

                        }catch (RuntimeException e){
                            e.printStackTrace();
                        }
                    }
                }, 50);
            }
        });



        return rootView;
    }



    public class TestAdapter extends DynamicGridAdapter<String> {


        @Override
        public View getViewItem(int position, View convertView, ViewGroup parent) {


//            TextView textView;
            ImageView imageView;
            if(convertView == null){


//                textView = new TextView(getActivity());
                imageView = new ImageView(getActivity());
                imageView.setMaxWidth(48);
                imageView.setMaxHeight(48);
                imageView.setPadding(3,3,3,3);
//                textView.setTextColor(Color.parseColor("#000000"));
//                textView.setGravity(Gravity.CENTER);


            }
            else{
                imageView = (ImageView)convertView;
            }

//            try {

                imageView.setImageDrawable(icons[position]);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }


//            textView.setText("\n\n"+getItem(position)+"\n\n");


//            int r = Integer.parseInt(getItem(position));
//            textView.setBackgroundColor(Color.rgb((r * 2) % 255, (r * 5) % 255, (r * 10) % 255));
            return imageView;
        }

    }


    private void setDeleteListener(){
        minidrawerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.miniDrawerAppsDBHelper.deleteApp(position);
            }
        });
    }

    private void setAppOpenListener(){
        minidrawerGrid.setOnItemClickListener(new DrawerClickListener(getActivity(),appList));
    }

}

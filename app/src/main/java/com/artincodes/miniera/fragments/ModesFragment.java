package com.artincodes.miniera.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.artincodes.miniera.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModesFragment extends Fragment {

    String TAG = "MODES FRAGMENT";

    Toolbar widgetToolbar;
    ImageView widgetButton;
    boolean widgetOpen =false;

    public ModesFragment() {

        // Required empty public constructor


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_modes, container, false);
        widgetToolbar = (Toolbar)rootView.findViewById(R.id.widget_toolbar);

        final RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(400);
        rotate.setInterpolator(new LinearInterpolator());


        widgetButton = (ImageView)rootView.findViewById(R.id.widget_drawer_button);
        final Button temp = (Button)rootView.findViewById(R.id.tempButton);

        widgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!widgetOpen) {
                    temp.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(),"Widget opening",Toast.LENGTH_SHORT).show();
                    widgetButton.startAnimation(rotate);
                    widgetOpen=true;
                }
                else {
                    temp.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(),"Widget Closing",Toast.LENGTH_SHORT).show();
                    widgetButton.startAnimation(rotate);
                    widgetOpen=false;
                }
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_container, new WeatherFragment())
                        .commit();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.todo_container, new TodoFragment())
                        .commit();

                if (Build.VERSION.SDK_INT < 18) {

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.music_container, new MusicFragmentv14())
                            .commit();
                }else if (Build.VERSION.SDK_INT < 19){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.music_container, new MusicFragmentv18())
                            .commit();
                }else {

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.music_container, new MusicFragment())
                            .commit();


                }

            }
        }, 0);

        return rootView;
    }


}

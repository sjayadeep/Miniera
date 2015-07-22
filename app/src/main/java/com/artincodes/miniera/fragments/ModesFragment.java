package com.artincodes.miniera.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.artincodes.miniera.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModesFragment extends Fragment {

    String TAG = "MODES FRAGMENT";


    public ModesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_modes, container, false);

        final LinearLayout modesLayout = (LinearLayout)rootView.findViewById(R.id.modes_layout);
        modesLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG,"Layout Touched" + event.getX() +", "+event.getY());
                int eventID = event.getAction();
                switch (eventID){
                    case MotionEvent.ACTION_MOVE:
//                        modesLayout.setY(event.getY());
                }
                return false;
            }
        });

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.weather_container,new WeatherFragment())
                .commit();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.todo_container,new TodoFragment())
                .commit();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.music_container,new MusicFragment())
                .commit();



        return rootView;
    }


}

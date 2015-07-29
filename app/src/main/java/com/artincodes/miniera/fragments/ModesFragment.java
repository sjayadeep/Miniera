package com.artincodes.miniera.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artincodes.miniera.MusicFragment;
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

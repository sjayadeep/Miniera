package com.artincodes.miniera.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artincodes.miniera.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModesFragment extends Fragment {


    public ModesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_modes, container, false);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.weather_container,new WeatherFragment())
                .commit();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.todo_container,new TodoFragment())
                .commit();

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .add(R.id.music_container,new MusicFragment())
//                .commit();



        return rootView;
    }


}

package com.artincodes.miniera.fragments;


import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.weather.WeatherDBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {




    private TextView temperatureText;
    private TextView temperatureDes;
    private TextView locationText;

//    private CircularProgressBar circularProgressBar;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_weather, container, false);


        temperatureText = (TextView) rootView.findViewById(R.id.temperature);
        temperatureDes = (TextView) rootView.findViewById(R.id.temperature_desc);
        locationText = (TextView) rootView.findViewById(R.id.location);
        Typeface robotoCond = Typeface.createFromAsset(getActivity().getAssets(), "roboto_thin.ttf");

        temperatureText.setTypeface(robotoCond);

//        circularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.progress_weather);
//        circularProgressBar.setVisibility(View.INVISIBLE);


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        WeatherDBHelper weatherDBHelper = new WeatherDBHelper(getActivity());
        Cursor cursor = weatherDBHelper.getWeather();
        cursor.moveToFirst();
        try {

            temperatureText.setText(cursor.getString(0) + (char) 0x00B0);
            temperatureDes.setText(cursor.getString(1));
            locationText.setText(cursor.getString(2));

        }catch (CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        cursor.close();


    }







}

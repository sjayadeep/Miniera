package com.artincodes.miniera.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.HomeViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */


public class HomeFragment extends Fragment {

    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("h:mm,EEEEEEEEE,d MMMMMMMMM", Locale.US);
    public static TextView timeTextView;
    public static TextView dateTextView;
    public static Typeface robotoCond;
    BroadcastReceiver timeBroadcastReciever;
    private MusicIntentReceiver myReceiver;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        timeTextView = (TextView)rootView.findViewById(R.id.text_time);
        dateTextView = (TextView)rootView.findViewById(R.id.text_date);
        robotoCond = Typeface.createFromAsset(getActivity().getBaseContext().getAssets(), "roboto_thin.ttf");

        myReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        getActivity().registerReceiver(myReceiver, filter);




        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                return false;
            }
        });
        timeTextView.setTypeface(robotoCond);
        timeTextView.setShadowLayer(2, 0, 1, Color.BLACK);
        dateTextView.setShadowLayer(2, 0, 1, Color.BLACK);

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setDateTime();

        timeBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    setDateTime();

                }
            }
        };

        getActivity().registerReceiver(timeBroadcastReciever, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void setDateTime() {

        String dateTime = _sdfWatchTime.format(new Date());
        String[] parts = dateTime.split(",");
        timeTextView.setText(parts[0]);
        dateTextView.setText(parts[2]+", "+parts[1]);
//        textDay.setText(parts[1]);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (timeBroadcastReciever != null)
            getActivity().unregisterReceiver(timeBroadcastReciever);
    }



    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                List<Fragment> fragments;

                switch (state) {
                    case 0:
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .remove(R.id.mode_fragmentContainer)
                        Log.d("HEADSET", "UnPlugged");

                        break;
                    case 1:
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .add(R.id.mode_fragmentContainer, new MusicFragmentv14())
//                                .addToBackStack("MINI DRAWER")
//                                .commit();
                        Log.d("HEADSET", "Plugged in");

                        break;
                    default:
                        Log.d("HEADSET", "I have no idea what the headset state is");
                }
            }
        }
    }

}

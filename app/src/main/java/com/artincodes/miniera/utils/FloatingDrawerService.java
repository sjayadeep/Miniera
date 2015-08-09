package com.artincodes.miniera.utils;


import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.artincodes.miniera.R;
import com.artincodes.miniera.fragments.MiniAppdrawerFragment;
import com.artincodes.miniera.utils.reveal.RevealLayout;

/**
 * Created by jayadeep on 6/8/15.
 */
public class FloatingDrawerService extends Service {
    private WindowManager windowManager;
    private ImageView floatingFaceBubble;
    private View miniDrawerView;
    private RevealLayout mRevealLayout;

    public void onCreate() {
        super.onCreate();
        floatingFaceBubble = new ImageView(this);
        //a face floating bubble as imageView
        floatingFaceBubble.setImageResource(R.mipmap.ic_info);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        miniDrawerView = layoutInflater.inflate(R.layout.fragment_mini_appdrawer, null);
//        final FrameLayout miniDrawerContainerLayout = new FrameLayout(this);




        //here is all the science of params
        final LayoutParams myParams = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x = 0;
        myParams.y = 100;
        // add a floatingfacebubble icon in window
//        miniDrawerContainerLayout.setLayoutParams(myParams);



        windowManager.addView(floatingFaceBubble, myParams);
        try {
            //for moving the picture on touch and slide
            floatingFaceBubble.setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //remove face bubble on long press
                    if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()) {
                        windowManager.removeView(floatingFaceBubble);
//                        stopSelf();
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            break;
                    }
                    return false;
                }
            });

            floatingFaceBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    miniDrawerView.setVisibility(View.VISIBLE);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}

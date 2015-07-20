package com.artincodes.miniera.utils.recent;

/**
 * Created by jayadeep on 15/7/15.
 */
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;


public class NotificationAccessibilityService extends AccessibilityService {

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            final String sourcePackageName = (String)event.getPackageName();
            Parcelable parcelable = event.getParcelableData();

            if (parcelable instanceof Notification) {
                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                    try {
                        final String notificationMsg = (String) messages.get(0);
                        Intent mIntent = new Intent(Constants.ACTION_CATCH_NOTIFICATION);
                        mIntent.putExtra(Constants.EXTRA_PACKAGE, sourcePackageName);
                        mIntent.putExtra(Constants.EXTRA_MESSAGE, notificationMsg);
                        this.getApplicationContext().sendBroadcast(mIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        }
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
    }


    public static final class Constants {

        public static final String EXTRA_MESSAGE = "extra_message";
        public static final String EXTRA_PACKAGE = "extra_package";
        public static final String ACTION_CATCH_NOTIFICATION = "com.artincodes.miniera.utils.recent.CATCH_NOTIFICATION";

    }

}

